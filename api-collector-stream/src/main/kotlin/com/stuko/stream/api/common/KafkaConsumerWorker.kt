package com.stuko.stream.api.common

@FunctionalInterface
interface KafkaConsumerWorker {
    fun work (map:MutableMap<String, Any>?) : Unit
}