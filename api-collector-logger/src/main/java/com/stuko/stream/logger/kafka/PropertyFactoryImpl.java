package com.stuko.stream.logger.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stuko.stream.logger.inf.PropertyFactory;

import java.util.Properties;

@Component
public class PropertyFactoryImpl implements PropertyFactory {

    @Value("${api.collector.kafka.bootstrap}")
    String brokerServer;
    @Value("${api.collector.kafka.consumer.group}")
    String consumerGroupName;
    @Value("${api.collector.kafka.wait}")
    int waitTime;
    @Value("${api.collector.kafka.partition}")
    int partition;
    @Value("${api.collector.kafka.replication}")
    int replication;

    @Override
    public Properties getKafkaProperties(Type type) {
        Properties properties = new Properties();
        if(type.equals(Type.ADMIN)){
            properties.put("bootstrap.servers",brokerServer);
            properties.put("enable.auto.commit", "true");
            properties.put("auto.offset.reset", "latest");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("compression.type", "none");
        }else if(type.equals(Type.CONSUMER)){
            properties.put("bootstrap.servers",brokerServer);
            properties.put("group.id", consumerGroupName);
            properties.put("enable.auto.commit", "true")    ;
            properties.put("auto.offset.reset", "latest");
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        }else if(type.equals(Type.PRODUCER)){
            properties.put("bootstrap.servers",brokerServer);
            properties.put("group.id", consumerGroupName);
            properties.put("enable.auto.commit", "true")    ;
            properties.put("auto.offset.reset", "latest");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("compression.type", "none");
        }
        return properties;
    }
}
