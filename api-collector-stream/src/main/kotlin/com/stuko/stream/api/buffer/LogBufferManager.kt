package com.stuko.stream.api.buffer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stuko.stream.api.common.KafkaUtils
import com.stuko.stream.api.dao.LogDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.Serializable
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("api.collector.kafka")
class LogBufferManagerConfig{
    var logger: Logger = LoggerFactory.getLogger(LogBufferManagerConfig::class.java)
    var bootstrap:String = ""
    var topic:String = ""
    var max:Int = 100
    var partition:Int = 1
    var replication:Int = 1
    var debug = "false"

    @Bean
    fun logBufferManager(@Qualifier("LogDAO") dao:LogDAO):AbstractLogBufferManager{
        logger.info("######## LOG BUFFER's Kafka Config ############")
        logger.info("bootstrap : ${this.bootstrap}")
        logger.info("topic : ${this.topic}")
        logger.info("partition : ${this.partition}")
        logger.info("partition : ${this.debug}")
        logger.info("replication : ${this.replication}")
        logger.info("######## LOG BUFFER's Kafka Config ############")

        /*
        // 20210818
        // 아래 컨슈머를 별도 Service ( api-collector-logger ) 로 분리 함.
        var logBufferConsumer = LogBufferConsumer(dao)
        logBufferConsumer.init(this.bootstrap,this.topic,this.max,this.partition, this.replication)
        logBufferConsumer.start()
        */

        var logBufferManager = LogBufferManager()
        logBufferManager.init(this.bootstrap,this.topic,this.max,this.partition, this.replication, this.debug)
        logger.info("######## LOG BUFFER's member vairables ############")
        logger.info("Broker Server is ${logBufferManager.brokerServer}")
        logger.info("Topic Name is ${logBufferManager.topicName}")
        logger.info("######## LOG BUFFER's member vairables ############")
        return logBufferManager
    }
}

class LogBufferConsumer(var dao:LogDAO) : AbstractLogBufferConsumer(){
    var logger: Logger = LoggerFactory.getLogger(LogBufferConsumer::class.java)
    var consumer: KafkaConsumer<Any, Any>? = null
    var scheduler:ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    override fun createKafka() {
        this.consumer = KafkaUtils.getConsumer(false,this.brokerServer, this.topicName,true)
    }

    override fun start(){

        if(this.consumer != null) {
            GlobalScope.launch {
                while(true) {
                    var records: ConsumerRecords<Any, Any> = consumer!!.poll(Duration.ofMillis(100));
                    try {
                        for (record: ConsumerRecord<Any, Any> in records) {
                            var json: String = record.value() as String

                            // logger.info("#### Logging data is ${json}")

                            val map = ObjectMapper().readValue<MutableMap<String, Any>?>(json) ?: continue
                            if(map["parameter"] is MutableMap<*, *>?) {
                                val parameter: MutableMap<String, Any>? = map["parameter"] as MutableMap<String, Any>?
                                if (parameter != null && map["db"] != null && map["topologyId"] != null) {
                                    val batchId: String =
                                        if (parameter["batchId"] == null) "NoBatchID" else parameter["batchId"] as String
                                    val topicName: String =
                                        if (parameter["topicName"] == null) "NoTopicName" else parameter["topicName"] as String
                                    val db = map["db"] as String
                                    val topologyId = map["topologyId"] as String
                                    val moduleName = map["moduleName"] as String
                                    val moduleType = map["moduleType"] as String

                                    dao.create(
                                        db,
                                        topicName,
                                        batchId,
                                        topologyId,
                                        topicName,
                                        moduleType,
                                        moduleName,
                                        map
                                    )
                                }
                            }

                        }
                    } catch (e: Exception) {
                        logger.error(e.toString())
                    }
                }
            }
        }
    }
}


class LogBufferManager(): AbstractLogBufferManager() , Serializable{
    val logger = LoggerFactory.getLogger(LogBufferManager::class.java)
    private var buffers: Queue<String> = LinkedList<String>()
    override fun createKafka() {
        KafkaUtils.getProducer( this.brokerServer, this.topicName )
        logger.debug("########## create Kafka #########")
        logger.debug("Already created producer is ${KafkaUtils.producerMap.keys.toString()}")
        logger.debug("########## create Kafka #########")
    }
    private fun produce(json:String){
        try {
            logger.debug("########## produce Kafka #########")
            logger.debug("Already created producer is ${KafkaUtils.producerMap.keys.toString()} and topicName is ${this.topicName}")
            logger.debug("########## produce Kafka #########")
            logger.debug("##### Kafka produce is called #####")
            logger.debug("produce!!! but broker server is :  ${this.brokerServer}")
            logger.debug("##### Kafka produce is called #####")
            KafkaUtils.getProducer( this.brokerServer, this.topicName )?.send(ProducerRecord(topicName, json))
        }catch(e:Exception){
            try{
                logger.error("#### LogBufferManager produce Error")
                logger.error(e.toString(),e)
                // KafkaUtils.getProducer( this.brokerServer, this.topicName )?.send(ProducerRecord(topicName, e.toString()))
            }catch(ee:Exception){logger.error(e.toString(),e)}
        }
    }
    override fun write(json:String){
        // logger.info("#### LogBufferManager write log : {}" , json)
        if("true".equals(debug)) this.add(json)
    }
    fun clear(){
        buffers.remove()
    }
    fun peek() :String{
        return buffers.peek()
    }
    fun add(element : String) : Unit{
        produce(element)
    }
}

