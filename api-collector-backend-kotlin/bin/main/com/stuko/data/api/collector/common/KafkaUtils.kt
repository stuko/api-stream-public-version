package com.stuko.data.api.collector.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ListTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


class KafkaUtils{

    companion object {
        private const val CONSUMER_GROUP_NAME : String = "consumerGroupName"
        val logger = LoggerFactory.getLogger(KafkaUtils::class.java)
        const val TOPIC_LOG: String = "LOG"
        var isAccessable: MutableMap<String,Boolean> = mutableMapOf()
        var CONSUMMING: MutableMap<String,Boolean> = mutableMapOf()
        var consumerMap: MutableMap<String, KafkaConsumer<Any, Any>?> = mutableMapOf()
        var producerMap: MutableMap<String, KafkaProducer<Any, Any>?> = mutableMapOf()

        public fun createTopic(kafkaProps: Properties?, topicName: String, numOfPartition: Int, replicationFactor: Int): Unit {
            // create result table by XXXXXXX
            if ("" != topicName) {
                var adminClient: AdminClient? = getAdminClient(kafkaProps)

                logger.info("AdminClient is created...")
                if (adminClient != null) {
                    logger.info("AdminClient is not null")
                    val result: ListTopicsResult = adminClient.listTopics()

                    logger.info("ListTopics is not null")
                    var kf: KafkaFuture<Set<String>> = result.names()
                    var names: Set<String>? = kf.get()
                    logger.info("ListTopics's names is {}", names)
                    if (names != null) {
                        logger.info("ListTopics's name is not null")
                        if (!names.contains(topicName)) {
                            val topicList: MutableList<NewTopic> = mutableListOf()
                            topicList.add(NewTopic(topicName, numOfPartition, replicationFactor.toShort()))
                            logger.info("Topic [{}] is created...", topicName)
                            adminClient.createTopics(topicList)
                            isAccessable.put(topicName, true)
                        }
                    }
                }
            }
        }

        private fun getProperties(type: String, sameGroup: Boolean): Properties? {
            return getProperties(type, "localhost:9092" , sameGroup)
        }

        public fun getProperties(type: String, serverIp: String?, sameGroup:Boolean ): Properties? {
            when (type) {
                "admin" -> {
                    return Properties().also {
                        it["bootstrap.servers"] = serverIp ?: "localhost";
                        it["enable.auto.commit"] = "true";
                        it["auto.offset.reset"] = "latest";
                        it["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer";
                        it["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
                        it["compression.type"] = "none"
                    }
                }
                "consumer" -> {
                    return Properties().also {
                        it["bootstrap.servers"] = serverIp ?: "localhost";
                        if(sameGroup) it["group.id"] = CONSUMER_GROUP_NAME;
                        else it["group.id"] = StringUtils.genKey("CG");
                        it["enable.auto.commit"] = "true";
                        it["auto.offset.reset"] = "latest";
                        it["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
                        it["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
                    }
                }
                "producer" -> {
                    return Properties().also {
                        it["bootstrap.servers"] = serverIp ?: "localhost";
                        it["enable.auto.commit"] = "true";
                        it["auto.offset.reset"] = "latest";
                        it["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer";
                        it["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
                        it["compression.type"] = "none"
                    }
                }
                else -> return null
            }
        }

        private fun getAdminClient(kafkaProps: Properties?): AdminClient? {
            return if (kafkaProps != null) AdminClient.create(kafkaProps)
            else {
                AdminClient.create(getProperties("admin", true))
            }
        }

        public fun getConsumer(isNew: Boolean, serverIp: String, topicName: String , sameGroup: Boolean): KafkaConsumer<Any, Any>? {
            return startConsumer(isNew, getProperties("consumer", serverIp, sameGroup), topicName)
        }

        private fun startConsumer(isNew: Boolean, kafkaProps: Properties?, topicName: String): KafkaConsumer<Any, Any>? {
            var consumer: KafkaConsumer<Any, Any>?
            if (!isNew && consumerMap.containsKey(topicName)) {
                consumer = consumerMap.get(topicName)
            } else {
                if (kafkaProps == null) {
                    consumer = KafkaConsumer<Any, Any>(getProperties("consumer" , true))
                } else consumer = KafkaConsumer<Any, Any>(kafkaProps)
                consumer.subscribe(listOf(topicName));
                if (!isNew) consumerMap[topicName] = consumer
            }
            return consumer
        }

        private fun produce(serverIp: String, topicName: String, map: MutableMap<*, *>?): Unit {
            var producer: KafkaProducer<Any, Any>? = getProducer(getProperties("producer", serverIp , true), topicName)
            var json: String = ObjectMapper().writeValueAsString(map)
            producer?.send(ProducerRecord(topicName, json))
        }

        public fun produce(producer: KafkaProducer<Any, Any>?,topicName:String, map: MutableMap<*, *>?): Unit {
            var json: String = ObjectMapper().writeValueAsString(map)
            producer?.send(ProducerRecord(topicName, json))
        }

        public fun getProducer(serverIp: String, topicName: String): KafkaProducer<Any, Any>? {
            return getProducer(getProperties("producer", serverIp , true), topicName)
        }

        private fun getProducer(kafkaProps: Properties?): KafkaProducer<Any, Any>? {
            return KafkaProducer<Any, Any>(kafkaProps)
        }

        private fun getProducer(kafkaProps: Properties?, topicName: String): KafkaProducer<Any, Any>? {
            var producer: KafkaProducer<Any, Any>?
            if (producerMap.containsKey(topicName)) {
                producer = producerMap[topicName]
            } else {
                producer = if (kafkaProps == null) {
                    getProducer(getProperties("producer" , true))
                } else {
                    logger.info("####### Kafka properties ########")
                    logger.info(kafkaProps.toString())
                    logger.info("####### Kafka properties ########")
                    KafkaProducer<Any, Any>(kafkaProps)
                }
                producerMap[topicName] = producer
            }
            return producer
        }

        public fun consume(consumer: KafkaConsumer<Any, Any>?, wait:Long, worker: KafkaConsumerWorker) : Unit{
            Thread {
                if (consumer != null) {
                    while (true) {
                        try {
                            var records: ConsumerRecords<Any, Any>? = consumer.poll(Duration.ofMillis(wait));
                            if (records != null) {
                                for (record in records) {
                                    var json: String = record.value() as String
                                    val map = ObjectMapper().readValue<MutableMap<String, Any>?>(json)
                                    logger.info("### Functional Worker working.....")
                                    worker.work(map)
                                }
                            }
                        } catch (e: Exception) {
                            logger.error(e.toString(), e)
                            Thread.sleep(5000)
                        }
                    }
                }
            }.start()
        }

        // topicName 은 실제 체크하기 위한 기준이 되는 정보 이다. 예를 들면  API Cert Key
        public fun checkPeriod(serverIp: String, consumer: KafkaConsumer<Any, Any>?, topicName: String, period: Int, percent: Int, gap: Long , wait:Long): Unit {
            if (consumer != null) {
                synchronized(CONSUMMING) {
                    if (!CONSUMMING.containsKey(topicName) || CONSUMMING[topicName] == false) {
                        CONSUMMING[topicName] = true
                        startConsumerAndProducer(serverIp, consumer, topicName, period, percent, gap, wait)
                    }
                }
            }
        }

        private fun startConsumerAndProducer(serverIp: String,myConsumer: KafkaConsumer<Any, Any>?, topicName: String, period: Int, percent: Int, gap: Long , wait:Long): Unit {

            Thread {
                logger.info("### START coroutine for Kafka Limit Manager Consumer")
                var consumer : KafkaConsumer<Any, Any>? = myConsumer
                var producer : KafkaProducer<Any, Any>? = getProducer(serverIp, topicName)
                while (true) {
                    try {
                        var records: ConsumerRecords<Any, Any>? = consumer!!.poll(Duration.ofMillis(wait));
                        isAccessable.put(topicName, true)
                        if (records != null) {
                            // val curOffset : Int? =
                            var curOffset : Long = 0
                            for(k : String in consumer.listTopics().keys){
                                if(k.equals(topicName)){
                                    var topicInfo = consumer.listTopics().get(k)
                                    topicInfo?.iterator()?.forEach { p : PartitionInfo ->
                                        var tp: TopicPartition = TopicPartition(p.topic(), p.partition())
                                        try {
                                            curOffset += consumer?.position(tp)!!
                                        }catch(e:Exception){
                                            logger.info("Consumer for $topicName can not find partition's position [${e.toString()}]")
                                        }
                                    }
                                }
                            }

                            var endOffset : Long? = consumer.listTopics()?.entries?.filter { it.key == topicName }
                                    ?.map {
                                        it.value.map { topicInfo -> TopicPartition(topicInfo.topic(), topicInfo.partition()) }
                                    }?.map {
                                        consumer?.endOffsets(it)?.values?.sum()
                                    }?.first()

                            val messageCount = endOffset?.minus(curOffset)

                            logger.info("### LimitManager ---------- checkLimit --------------")
                            logger.info("### LimitManager endOffset count : {}  ", endOffset)
                            logger.info("### LimitManager curOffset count : {}  ", curOffset)
                            logger.info("### LimitManager concurrent message count for $wait millsec : {}  ", messageCount)
                            logger.info("### LimitManager time gap to must limit  : {}  ", gap)
                            logger.info("### LimitManager limit percent  : {}  ", percent)
                            logger.info("### LimitManager ---------- checkLimit --------------")

                            if (messageCount != null) {
                                if (messageCount >= (gap * percent) / 100) {
                                    isAccessable.put(topicName, false)
                                }else {
                                    isAccessable.put(topicName, true)
                                }
                            }else{
                                isAccessable.put(topicName, true)
                            }

                            for (record in records) {
                                var json: String = record.value() as String
                                val map = ObjectMapper().readValue<MutableMap<String, Any>?>(json)
                                var time = map?.get("time") as Long
                                var cur = System.currentTimeMillis().toLong()
                                // 아직 지나지 않았으면
                                logger.info("### LimitManager ---------- Limit Compare in Queue --------------")
                                logger.info("### LimitManager consume from queue = {} ", time)
                                logger.info("### LimitManager current time  = {} ", cur)
                                logger.info("### LimitManager time gap  = {} ", (cur - time))
                                logger.info("### LimitManager 키  = {} ", map.get("키"))
                                logger.info("### LimitManager 현재일시  = {} ", map.get("현재일시"))
                                logger.info("### LimitManager 입력일시  = {} ", map.get("입력일시"))
                                logger.info("### LimitManager limit period  = {} ", period)

                                logger.info("### LimitManager ---------- Limit Compare in Queue --------------")
                                if ((cur - time) <= period) {
                                    var sdf:SimpleDateFormat = SimpleDateFormat("yyyy년MM월dd일HH:mm:ss")
                                    logger.info("### LimitManager cur date : {}" , sdf.format(Date(cur)))
                                    logger.info("### LimitManager queue date : {}" , sdf.format(Date(time)))
                                    map.put("현재일시",sdf.format(Date(cur)))
                                    map.put("입력일시",sdf.format(Date(time)))
                                    map.put("키",StringUtils.genKey("LM"))
                                    logger.info("### LimitManager reproduce : {}" , map)
                                    produce(producer, topicName, map)
                                } else {
                                    logger.info("### LimitManager do not reproduce")
                                }
                            }
                        } else {
                            logger.info("### LimitManager consumer record is null , zero")
                        }
                    } catch (e: Exception) {
                        logger.error(e.toString(),e)
                        logger.info("### LimitManager Error : {}", e.toString())
                        Thread.sleep(5000)
                        try{
                            if(consumer == null) consumer = KafkaUtils.getConsumer(true , serverIp, topicName , true)
                        }catch(ee:Exception){
                            logger.info("Consummer is Error ${ee.toString()}")
                        }
                        try{
                            if(producer == null) producer = getProducer(serverIp, topicName)
                        }catch(ee:Exception){
                            logger.info("Producer is Error ${ee.toString()}")
                        }
                    }
                }
            }.start()
        }

        // percent default = 100
        public fun checkLimit(serverIp: String,topicName: String): Boolean {
            if (isAccessable.containsKey(topicName) && isAccessable[topicName]!!) {
                var m = mutableMapOf<Any, Any>()
                m["time"] = System.currentTimeMillis().toLong()
                produce(serverIp, topicName, m)
                return true
            } else {
                return false
            }
        }
    }
}