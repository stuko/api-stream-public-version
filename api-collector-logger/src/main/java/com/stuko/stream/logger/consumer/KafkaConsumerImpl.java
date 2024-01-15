package com.stuko.stream.logger.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stuko.stream.logger.inf.*;

import java.time.Duration;
import java.util.Arrays;

public class KafkaConsumerImpl  implements Consumer {

    static Logger logger = LoggerFactory.getLogger(KafkaConsumerImpl.class);

    public KafkaConsumerImpl(String topic){
        this.topicName = topic;
    }

    String topicName;
    KafkaConsumerAction action;

    @Autowired
    KafkaConsumerFactory kafkaConsumerFactory;
    KafkaConsumer consumer;
    boolean stop = false;

    @Override
    public void init(KafkaConsumerAction action) {
        this.action = action;
        consumer = kafkaConsumerFactory.create(PropertyFactory.Type.CONSUMER);
        consumer.subscribe(Arrays.asList(topicName));
    }

    @Override
    public void stop() {
        this.stop = true;
    }

    @Override
    public void consume() {
        logger.info("start consuming....");
        new Thread(()->{
            while(!stop) {
                ConsumerRecords<String,Object> records = consumer.poll(Duration.ofMillis(100));
                if(records != null){
                    records.forEach(c->{
                        Object object = c.value();
                        action.act(object);
                    });
                }
            }
            logger.info("stop consuming....");
        }).start();
    }
}
