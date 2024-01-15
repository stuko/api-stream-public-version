package com.stuko.data.api.collector.apicollectorscheduler.service

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class KafkaProducer {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    companion object: Log

    fun sendMessage(topicName: String, messageKey: String?, message: String) {
        var msg = "{\"batchId\":\"$message\"}"
        val messageBuilder: Message<String> = MessageBuilder
                .withPayload(msg)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build()
        kafkaTemplate.send(messageBuilder)
                .addCallback({
                    log.info("Send message=[$message] with offset=[${it!!.recordMetadata.offset()}")
                },{
                    log.info("Unable to send message=[$message] due to ${it.message}")
                })


    }
}