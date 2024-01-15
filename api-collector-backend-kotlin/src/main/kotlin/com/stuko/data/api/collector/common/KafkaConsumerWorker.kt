package com.stuko.data.api.collector.common

@FunctionalInterface
interface KafkaConsumerWorker {
    fun work (map:MutableMap<String, Any>?) : Unit
}