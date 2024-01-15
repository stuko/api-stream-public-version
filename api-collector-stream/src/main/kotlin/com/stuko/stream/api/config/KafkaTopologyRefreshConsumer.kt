package com.stuko.stream.api.config

import com.stuko.stream.api.common.KafkaUtils
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// @Configuration
// @EnableConfigurationProperties
// @ConfigurationProperties("api.collector.kafka")
class KafkaTopologyRefreshConsumerConfig {

    var logger: Logger = LoggerFactory.getLogger(KafkaTopologyRefreshConsumerConfig::class.java)
    var bootstrap:String = ""
    var refreshTopic:String = "TOPOLOGY_REFRESH"
    var max:Int = 100
    var partition:Int = 1
    var replication:Int = 1

    @Bean(name= arrayOf("kafkaProducer"))
    fun createProducer(): KafkaProducer<Any, Any>?{
        return KafkaUtils.getProducer( this.bootstrap, this.refreshTopic )
    }

    @Bean(name= arrayOf("kafkaConsumer"))
    fun createConsumer(): KafkaConsumer<Any, Any>?{
        return KafkaUtils.getConsumer(false,this.bootstrap, this.refreshTopic,true)
    }

}
