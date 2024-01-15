package com.stuko.data.api.collector.apicollectorscheduler.service

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@EnableKafka
@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapServer:String

    @Value("\${spring.kafka.producer.key-serializer}")
    lateinit var keySerializer: String

    @Value("\${spring.kafka.producer.value-serializer}")
    lateinit var valueSerializer: String

    @Bean
    fun producerFactory():ProducerFactory<String, String> {
        val configProps:MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServer
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate():KafkaTemplate<String, String> = KafkaTemplate(producerFactory())
}