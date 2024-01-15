package com.stuko.stream.logger.inf;

import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface KafkaConsumerFactory {
    PropertyFactory getPropertyFactory();
    KafkaConsumer create(PropertyFactory.Type type);
}
