package com.stuko.stream.api.module.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.KafkaUtils
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.common.VarUtils
import com.stuko.stream.api.module.IModule
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.storm.spout.SpoutOutputCollector
import org.apache.storm.task.TopologyContext
import org.apache.storm.topology.IRichSpout
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseRichSpout
import org.apache.storm.tuple.Fields
import org.apache.storm.tuple.Values
import java.time.Duration
import java.util.*


// .\kafka-delete-records.bat --bootstrap-server localhost:9092 --offset-json-file ..\..\config\delete.json
// .\kafka-console-producer.bat --broker-list localhost:9092 --topic topicName
abstract class AbstractSpoutModule(override var moduleType: IModule.KIND, override var moduleKey: String, override var moduleCount: Int,
                                   var logBufferManager: AbstractLogBufferManager) : ISpoutModule, IRichSpout {
    companion object : Log
    private var end:Boolean = false

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

    override fun getProperty(name : String): String {
        return ""
    }

    var name: String = ""
    var async:Boolean = false
    var consumerTopic: String = ""
    var producerTopic: String = ""
    var brokerServer: String = ""
    var cunsumerGroupName: String = ""
    var autoCommit: String = ""
    var autoOffsetReset: String = ""
    var keySerializer: String = ""
    var valueSerializer: String = ""
    var keyDeserializer: String = ""
    var valueDeserializer: String = ""
    var numOfPartition: String= ""
    var replicationFactor: String = ""

    public override var topologyId:String = ""
        get() = field
        set(value) {
            field = value
        }

    public override var collection:String = ""
        get() = field
        set(value) {
            field = value
        }

    public override var db:String = ""
        get() = field
        set(value) {
            field = value
        }

    public override var moduleName:String = ""
        get() = field
        set(value) {
            field = value
        }

    var kafkaConsumerProps: Properties? = null
        get() = field
        set(value) {
            field = value
        }

    var kafkaProducerProps: Properties? = null
        get() = field
        set(value) {
            field = value
        }

    var consumer: KafkaConsumer<String, Any>? = null

    private var _collector:  SpoutOutputCollector? = null
    // Sput : First Batch

    override fun nextTuple() {
        logger.debug("###########################")
        logger.debug("nextTuple Start.....")
        logger.debug("###########################")
        if(this.consumer != null) {
            logger.debug("consumer is not null...${this.consumerTopic}")
            var records: ConsumerRecords<String, Any> = this.consumer!!.poll(Duration.ofMillis(100));
            try {
                logger.debug("Read from Kafka, Success !!!")
                if(records != null) logger.debug("Read Records's count is ${records.count()}")
                else logger.debug("Read Records's count is ZERO")
                for (record: ConsumerRecord<String, Any> in records) {
                    logger.info("Topic: ${record.topic()}" +
                            ", Partition: ${record.partition()}" +
                            ", Offset: ${record.offset()}" +
                            ", Key: ${record.key()}" +
                            ", Value: ${record.value()}\n")
                    var json: String = record.value() as String
                    logger.info("--------- Spout's first json ---------------")
                    logger.info("{}",json)
                    logger.info("--------- Spout's first json ---------------")
                    json = VarUtils.replaceVar(json,null)
                    val map = ObjectMapper().readValue<MutableMap<String, Any>?>(json)
                    if (map != null) {
                        emitMap(map)
                    }else{
                        logger.info("Consumer read Null Data(not Map) from Kafka")
                    }
                }

            }catch(e:Exception){
                logger.error(e.toString(),e)
            }finally{
            }
            // if(records.isEmpty()) logger.info("read no record from kafka ......... ")
        }else{
            logger.info("Sorry, Consumer is Null of ${this.consumerTopic}")
        }
    }

    public fun emitMap(map: MutableMap<String, Any>) {
        if (!map.containsKey("batchId")) map.put("batchId", "TMP_BAT_" + StringUtils.genKey(this.moduleKey))
        logger.info("my topology id is : {}, {} , {} ",
            map.get("batchId") as String,
            this.topologyId,
            this.consumerTopic)
        map["SpoutKey"] = StringUtils.genKey(this.moduleKey)
        map["SpoutTxTime"] = StringUtils.getTime()
        val tracer = map["BoltTracer"] ?: ""
        map["BoltTracer"] = tracer.toString() + "->" + this.moduleKey
        map["topicName"] = this.consumerTopic
        this.log(map, null)
        val result: MutableMap<String, Any>? = this.execute(map, _collector)
        _collector?.emit(Values(map))
        this.log(map, result)
    }

    fun initProperty(){
        this.getProperty().forEach {m->
            val name:String = m.get("name") as String;
            val value:String = m.get("value") as String;
            if("brokerServer".equals(name)) this.brokerServer = value;
            else if("cunsumerGroupName".equals(name)) this.cunsumerGroupName = value;
            else if("autoCommit".equals(name)) this.autoCommit = value;
            else if("autoOffsetReset".equals(name)) this.autoOffsetReset = value;
            else if("keySerializer".equals(name)) this.keySerializer = value;
            else if("valueSerializer".equals(name)) this.valueSerializer = value;
            else if("keyDeserializer".equals(name)) this.keyDeserializer = value;
            else if("valueDeserializer".equals(name)) this.valueDeserializer = value;
            else if("consumerTopic".equals(name)) this.consumerTopic = value;
            else if("producerTopic".equals(name)) this.producerTopic = value;
            else if("numOfPartition".equals(name)) this.numOfPartition = value;
            else if("replicationFactor".equals(name)) this.replicationFactor = value;
        }

    }

    override fun init(){
        this.initProperty();
        if(this.brokerServer == null || "".equals(this.brokerServer)
                || this.cunsumerGroupName == null || "".equals(this.cunsumerGroupName)
                || this.autoCommit == null || "".equals(this.autoCommit)
                || this.autoOffsetReset == null || "".equals(this.autoOffsetReset)
        ){
            logger.info("Kafka configuration value is null")
            logger.info("#### Spout Info of Kafka consumer ####")
            logger.info("brokerServer : [{}]", this.brokerServer )
            logger.info("cunsumerGroupName : [{}]", this.cunsumerGroupName )
            logger.info("autoCommit : [{}]", this.autoCommit )
            logger.info("autoOffsetReset : [{}]", this.autoOffsetReset )
            logger.info("keySerializer : [{}]", this.keySerializer )
            logger.info("valueSerializer : [{}]", this.valueSerializer )
            logger.info("keyDeserializer : [{}]", this.keyDeserializer )
            logger.info("valueDeserializer : [{}]", this.valueDeserializer )
            logger.info("#### Spout Info of Kafka consumer ####")
            return;
        }else{
            logger.info("#### Spout Info of Kafka consumer ####")
            logger.info("brokerServer : [{}]", this.brokerServer )
            logger.info("cunsumerGroupName : [{}]", this.cunsumerGroupName )
            logger.info("autoCommit : [{}]", this.autoCommit )
            logger.info("autoOffsetReset : [{}]", this.autoOffsetReset )
            logger.info("keySerializer : [{}]", this.keySerializer )
            logger.info("valueSerializer : [{}]", this.valueSerializer )
            logger.info("keyDeserializer : [{}]", this.keyDeserializer )
            logger.info("valueDeserializer : [{}]", this.valueDeserializer )
            logger.info("#### Spout Info of Kafka consumer ####")
        }

        //Set the instance collector to the one passed in
        this.kafkaProducerProps = Properties().also {
            it.put("bootstrap.servers", this.brokerServer);
            it.put("group.id", this.cunsumerGroupName);
            it.put("enable.auto.commit", (if(this.autoCommit == null) "true" else this.autoCommit).toBoolean());
            it.put("auto.offset.reset", if(this.autoOffsetReset == null) "none" else this.autoOffsetReset);
            it.put("key.serializer", this.keySerializer);
            it.put("value.serializer", this.valueSerializer)
        };

        this.kafkaConsumerProps = Properties().also {
            it.put("bootstrap.servers", this.brokerServer);
            it.put("group.id", this.cunsumerGroupName);
            it.put("enable.auto.commit", (if(this.autoCommit == null) "true" else this.autoCommit).toBoolean());
            it.put("auto.offset.reset", if(this.autoOffsetReset == null) "none" else this.autoOffsetReset);
            it.put("key.deserializer", this.keyDeserializer);
            it.put("value.deserializer", this.valueDeserializer)
        };
        logger.info("before create topic")
        KafkaUtils.createTopic(this.kafkaConsumerProps, this.consumerTopic,this.numOfPartition.toInt(), this.replicationFactor.toInt())
        logger.info("after create topic")
        logger.info("------- Spout open ---------")
        logger.info("Spout open is ready")
        logger.info("------- Spout open ---------")
    }

    override fun open(conf: MutableMap<String, Any>?, context: TopologyContext?, collector: SpoutOutputCollector?) {
        logger.info("Kafka Info : {}", kafkaConsumerProps.toString())
        this.consumer = KafkaConsumer(kafkaConsumerProps)
        this.consumer?.subscribe(Arrays.asList(this.consumerTopic));
        this._collector = collector
        logger.info("################################################")
        logger.info("Collector Open to consumer's topic : {}", this.consumerTopic)
        logger.info("Consumer ID : {}", this.consumer.toString())
        logger.info("################################################")
    }

    override fun declareOutputFields(declarer: OutputFieldsDeclarer?) {
        declarer?.declare(Fields(this.name));
    }

    abstract override fun execute(map: MutableMap<String, Any>?, collector: SpoutOutputCollector?): MutableMap<String, Any>?

    override fun ack(msgId: Any?) {
        logger.info("ack ")
    }

    override fun fail(msgId: Any?) {
        logger.info("fail ")
    }

    override fun getComponentConfiguration(): MutableMap<String, Any> {
        logger.info("getComponentConfiguration ")
        return mutableMapOf()
    }

    override fun close() {
        logger.info("close ")
    }

    override fun activate() {
        logger.info("activate ")
    }

    override fun deactivate() {
        logger.info("deactivate ")
    }

    fun log(parameter: MutableMap<String, Any>?, result: MutableMap<String, Any>?) {
        var logMap = mutableMapOf<String,Any?>()
        logMap["parameter"] = parameter
        logMap["result"] = result
        logMap["db"] = this.db
        logMap["moduleName"] = this.moduleName
        logMap["moduleType"] = this.moduleType
        logMap["topologyId"] = this.topologyId
        logMap["collection"] = this.collection
        this.logBufferManager.write(ObjectMapper().writeValueAsString(logMap))
    }

}
