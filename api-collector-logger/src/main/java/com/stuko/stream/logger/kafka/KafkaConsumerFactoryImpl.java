package com.stuko.stream.logger.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stuko.stream.logger.inf.KafkaConsumerFactory;
import com.stuko.stream.logger.inf.PropertyFactory;

import java.util.Properties;

@Component
public class KafkaConsumerFactoryImpl implements KafkaConsumerFactory {

    @Autowired
    PropertyFactory propertyFactory;

    @Override
    public PropertyFactory getPropertyFactory() {
        return propertyFactory;
    }

    @Override
    public KafkaConsumer create(PropertyFactory.Type type) {
        Properties p = propertyFactory.getKafkaProperties(type);
        return new KafkaConsumer(p);
    }
}
