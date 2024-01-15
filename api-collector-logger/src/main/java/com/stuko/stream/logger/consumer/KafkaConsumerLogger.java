package com.stuko.stream.logger.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stuko.stream.logger.inf.Writer;

@Component
public class KafkaConsumerLogger extends KafkaConsumerImpl {
    static Logger logger = LoggerFactory.getLogger(KafkaConsumerLogger.class);
    @Autowired
    Writer writer;
    public KafkaConsumerLogger(@Value("${api.collector.kafka.topic.log}") String topic) {
        super(topic);
    }
    public Writer getWriter() {
        return writer;
    }
}
