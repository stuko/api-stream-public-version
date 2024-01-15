package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.KafkaConsumerWorker
import com.stuko.stream.api.common.KafkaUtils
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.module.IModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.storm.task.OutputCollector
import org.apache.storm.topology.BasicOutputCollector
import org.apache.storm.tuple.Values

class WaitModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                 logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    companion object : Log

    var topicName : String? = ""
        get() = field
        set(value) {
            field = value
        }

    var serverIp : String? = ""
        get() = field
        set(value) {
            field = value
        }

    var wait : Long = 600000
        get() = field
        set(value) {
            field = value
        }
    var producer: KafkaProducer<Any, Any>? = null

    override fun afterPrepare(collector: OutputCollector?): Unit{
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        try {
            logger.info("WaitModule.... CONSUMMING Synchronized")
            if (KafkaUtils.CONSUMMING.containsKey(this.topicName) == false || KafkaUtils.CONSUMMING.get(topicName) == false) {
                logger.info("WaitModule.... CONSUMMING is NULL")
                KafkaUtils.CONSUMMING.put(this.topicName!!, true)
                logger.info("WaitModule.... CONSUMMING set TRUE")
                logger.info("WaitModule.... Let's start Consumer")

                KafkaUtils.consume(KafkaUtils.getConsumer(true, this.serverIp ?: "localhost:9092", this.topicName
                        ?: this.topologyId + this.moduleKey, true, 1), wait, object : KafkaConsumerWorker {
                    override fun work(objMap: MutableMap<String, Any>?) {
                        try {
                            logger.info("### Functional Worker WaitModuel was called.....")
                            collector!!.emit(Values(objMap))
                            log(null, objMap)
                        }catch(e:Exception){
                            logger.error(e.toString(),e)
                            if(objMap != null) objMap.put("ERROR",e.toString())
                            log(null, objMap)
                        }
                    }
                })

                if(producer == null) producer = serverIp?.let { KafkaUtils.getProducer(it, topicName!!) }

            }else{
                logger.info("WaitModule.... Already contains topic $topicName")
            }
            logger.info("WaitModule.... write log parameter {} ", map)
            log(map, null)
            if(producer == null) producer = serverIp?.let { KafkaUtils.getProducer(it, topicName!!) }
            KafkaUtils.produce(producer, this.topicName ?: this.topologyId + this.moduleKey, map)
        }catch(e: Exception){
            logger.error(e.toString(),e)
            map?.put("ERROR",e.toString())
            log(null, map)
        }
        return map
    }

    override fun start() :Boolean {
        try {
            val wt: String = this.getProperty("wait")
            val ip: String = this.getProperty("brokerServer")
            var partition: String = this.getProperty("numOfPartition")
            var replication: String = this.getProperty("replicationFactor")
            partition = if (!"".equals(partition)) {partition} else "3"
            replication = if (!"".equals(replication)) {replication} else "3"

            this.wait = wt.toLong()
            this.serverIp = ip
            this.topicName = this.topologyId + this.moduleKey
            logger.info("--------- WaitModule ------------")
            logger.info("wait : $wait")
            logger.info("serverIp : $ip")
            logger.info("topic : $topicName")
            logger.info("--------- WaitModule ------------")
            KafkaUtils.createTopic(KafkaUtils.getProperties("admin", serverIp , true), this.topicName!!, partition.toInt(), replication.toInt())
            return true
        }catch(e:Exception){
            logger.error(e.toString(),e)
            return false
        }finally{
            this.async = true
        }
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }

}