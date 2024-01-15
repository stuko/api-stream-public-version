package com.stuko.stream.logger.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stuko.stream.logger.inf.Caller;

public class KafkaConsumerStreamRefresh extends KafkaConsumerImpl {
    static Logger logger = LoggerFactory.getLogger(KafkaConsumerStreamRefresh.class);
    @Autowired
    Caller caller;
    public KafkaConsumerStreamRefresh(@Value("${api.collector.kafka.topic.refresh}") String topic) {
        super(topic);
    }
    public Caller getCaller() {
        return caller;
    }
}
