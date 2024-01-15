package com.stuko.stream.api.module.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.KafkaUtils
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.module.IModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.storm.task.OutputCollector
import org.apache.storm.task.TopologyContext
import org.apache.storm.topology.BasicOutputCollector
import org.apache.storm.topology.IRichBolt
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseBasicBolt
import org.apache.storm.tuple.Fields
import org.apache.storm.tuple.Tuple
import org.apache.storm.tuple.Values
import java.util.*

abstract class AbstractBoltModule(override var moduleType: IModule.KIND, override var moduleKey: String, override var moduleCount: Int , var logBufferManager: AbstractLogBufferManager) : IBoltModule , IRichBolt {

    companion object : Log
    var outPutCollector: OutputCollector? = null
    private var end:Boolean = false
    var name: String = ""
    var brokerServer: String = ""
    var autoCommit: String = ""
    var autoOffsetReset: String = ""
    var keySerializer: String = ""
    var valueSerializer: String = ""
    var numOfPartition: String= "1"
    var replicationFactor: String = "1"
    var producerTopic: String= ""
    var async:Boolean = false
    public override var topologyId:String = ""
    public override var collection:String = ""
    public override var db:String = ""
    public override var moduleName:String = ""
    var kafkaProps: Properties? = null

    override fun setEnd(end: Boolean) {
        this.end = end
    }
    override fun isEnd() :Boolean {
        return this.end
    }

    private var property: MutableList<MutableMap<String,Any>> = mutableListOf()
    override fun setProperty(p: MutableList<MutableMap<String,Any>>) {
        this.property = p
    }

    override fun getProperty(): MutableList<MutableMap<String,Any>> {
        return this.property
    }

    override fun getProperty(name:String): String {
        var result : String = ""
        for(m in this.getProperty()){
            if(name == m["name"] as String){
                result= m["value"] as String
            }
        }
        return result
    }

    override fun prepare(topoConf: MutableMap<String, Any>?, topologyContext: TopologyContext? , outputCollector: OutputCollector) {
        logger.info("#### Bolt prepared...")
        this.outPutCollector = outputCollector
        this.afterPrepare(this.outPutCollector)
    }

    private fun initProperty(){
        this.getProperty().forEach {m->
            val name:String = m["name"] as String;
            val value:String = m["value"] as String;
            when (name) {
                "brokerServer" -> this.brokerServer = value
                "autoCommit" -> this.autoCommit = value
                "autoOffsetReset" -> this.autoOffsetReset = value
                "keySerializer" -> this.keySerializer = value
                "valueSerializer" -> this.valueSerializer = value
                "producerTopic" -> this.producerTopic = value
                "numOfPartition" -> this.numOfPartition = value
                "replicationFactor" -> this.replicationFactor = value
            };
        }
    }

    override fun init(){
        initProperty()
        if(this.brokerServer == null || "" == this.brokerServer
            || this.autoCommit == null || "" == this.autoCommit
            || this.autoOffsetReset == null || "" == this.autoOffsetReset
        ){
            logger.info("Kafka configuration value is null")
            logger.info("#### Bolt Info of Kafka producer ####")
            logger.info("brokerServer : [{}]", this.brokerServer )
            logger.info("autoCommit : [{}]", this.autoCommit )
            logger.info("autoOffsetReset : [{}]", this.autoOffsetReset )
            logger.info("keySerializer : [{}]", this.keySerializer )
            logger.info("valueSerializer : [{}]", this.valueSerializer )
            logger.info("#### Bolt Info of Kafka producer ####")
            return;
        }else{
            logger.info("#### Bolt Info of Kafka producer ####")
            logger.info("brokerServer : [{}]", this.brokerServer )
            logger.info("autoCommit : [{}]", this.autoCommit )
            logger.info("autoOffsetReset : [{}]", this.autoOffsetReset )
            logger.info("keySerializer : [{}]", this.keySerializer )
            logger.info("valueSerializer : [{}]", this.valueSerializer )
            logger.info("#### Bolt Info of Kafka producer ####")
        }

        //Set the instance collector to the one passed in
        kafkaProps = Properties().also {
            it["bootstrap.servers"] = this.brokerServer;
            it["enable.auto.commit"] = (if(this.autoCommit == null) "true" else this.autoCommit).toBoolean();
            it["auto.offset.reset"] = if(this.autoOffsetReset == null) "none" else this.autoOffsetReset;
            it["key.serializer"] = this.keySerializer;
            it["value.serializer"] = this.valueSerializer
            it["compression.type"] = "none"
        };

        KafkaUtils.createTopic(this.kafkaProps, this.producerTopic,this.numOfPartition.toInt(), this.replicationFactor.toInt())
    }

    override fun execute(input: Tuple?) {
        if (input != null) {
            try {
                logger.info("input by bolt is not null...................")
                if (input?.size()!! > 0) {
                    logger.info("input by bolt's input size is {}", input?.size())
                    for (x in 0 until input?.size()) {
                        logger.info("input by bolt's input[{}]", x)
                        try {
                            if (input?.getValue(x) is MutableList<*>?) {
                                var list: MutableList<*>? = input?.getValue(x) as MutableList<*>?
                                logger.info("input data is List and size is {}", list?.size)
								list?.forEach { map ->
                                    doNextJob(map as MutableMap<*, *>?, input, this.outPutCollector)
                                }

                            } else if (input?.getValue(x) is MutableMap<*, *>?) {
                                var map: MutableMap<*, *>? = input?.getValue(x) as MutableMap<*, *>?
                                doNextJob(map, input, this.outPutCollector)
                            }
                        } catch (e: Exception) {
                            logger.error(e.toString(), e)
                            var m = mutableMapOf<String, Any>()
                            m["ERROR"] = e.toString()
                            this.log(m, null)
                        } finally {

                        }
                    }
                } else {
                    logger.info("input by bolt's input size is zero")
                }
            }finally{
                this.outPutCollector?.ack(input)
            }
        }else{
            logger.info("$$$$$$$$$$ Bolt's input is NULL ")
        }
    }

    private fun doNextJob(map : MutableMap<*, *>?, input: Tuple?, collector: OutputCollector?){
        if (map != null) {
			logger.info("this map data is {}", map)
            if (map is MutableMap<*, *>) {
                // @Suppress("UNCHECKED_CAST")
                var parameter = map as MutableMap<String, Any>?
                try {
                    val gson: Gson = Gson().newBuilder().serializeSpecialFloatingPointValues().create()
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    parameter = gson.fromJson(gson.toJson(parameter), mapType)
                    parameter?.put("BoltKey", StringUtils.genKey(this.moduleKey))
                    parameter?.put("BoltTxTime", StringUtils.getTime())
                    val tracer = parameter?.get("BoltTracer") ?: ""
                    parameter?.put("BoltTracer", tracer.toString() + "->" + this.moduleKey)

                    var result: MutableMap<String, Any>? = null
                    try {
                        result = this.execute(parameter, collector)
                    } catch (e: Exception) {
                        logger.error(e.toString(), e)
                        parameter?.put("ERROR", e.toString())
                        this.log(parameter, null)
                        if (!async && collector != null) collector!!.emit(Values(parameter))
                        if (this.isEnd()) this.finish(input, collector)
                        return
                    }

                    try {
                        if (!async && collector != null) collector!!.emit(Values(parameter))
                        if (this.isEnd()) this.finish(input, collector)
                    } catch (e: Exception) {
                        logger.error(e.toString(),e)
                    }
                }catch(e:Exception){
                    logger.error(e.toString(),e)
                    var m = mutableMapOf<String,Any>()
                    m["ERROR"] = e.toString()
                    this.log(m, null)
                }
            }
        }
    }

    override fun declareOutputFields(declarer: OutputFieldsDeclarer?) {
        declarer?.declare(Fields(this.moduleName));
    }

    override fun getComponentConfiguration(): MutableMap<String, Any> {
        logger.info("#### getComponentConfuguration().....")
        return mutableMapOf()
    }

    private fun finish(input: Tuple?, collector: OutputCollector?) {
        try {
            logger.info("############ EndModule finish ###############")
            logger.info("Finish called......")
            logger.info("############ EndModule finish ###############")
            if(this.producerTopic != null && !"".equals(this.producerTopic)) {
                var producer: KafkaProducer<String, String> = KafkaProducer<String, String>(kafkaProps)
                if(input?.size()!! > 0){
                    for(x in 0 until input?.size()){
                        var map: MutableMap<*, *>? = input?.getValue(x) as MutableMap<*, *>?
                        var json: String = ObjectMapper().writeValueAsString(map)
                        producer.send(ProducerRecord(this.producerTopic, json))
                    }
                }
            }
        }catch(e : Exception){
            logger.error(e.toString(),e)
        }
    }

    fun log(parameter: MutableMap<String, Any>?, result: MutableMap<String, Any>?) {
        this.log(parameter,result,false)
    }

    fun log(parameter: MutableMap<String, Any>?, result: MutableMap<String, Any>? , writeRoot : Boolean) {
        var logMap = mutableMapOf<String,Any?>()
        if(writeRoot) result?.let { logMap.putAll(it) }
        else {
            logMap["result"] = result
        }
        logMap["parameter"] = parameter
        logMap["db"] = this.db
        logMap["moduleName"] = this.moduleName
        logMap["moduleType"] = this.moduleType
        logMap["topologyId"] = this.topologyId
        logMap["collection"] = this.collection
        this.logBufferManager.write(ObjectMapper().writeValueAsString(logMap))
    }


    abstract override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>?
    abstract fun afterPrepare(collector: OutputCollector?): Unit
}