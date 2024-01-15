package com.stuko.stream.logger;

import com.stuko.stream.logger.consumer.KafkaConsumerLogger;
import com.stuko.stream.logger.consumer.KafkaConsumerStreamRefresh;
import com.stuko.stream.logger.inf.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableAutoConfiguration
public class LoggerApplication {

    @Autowired
    KafkaConsumerLogger consumerLogger;
    static Logger logger = LoggerFactory.getLogger(LoggerApplication.class);
    public static void main(String[] args){
        SpringApplication.run(LoggerApplication.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomething(){
        logger.info("Logging consumming.. init");
        consumerLogger.init((object)->{
            consumerLogger.getWriter().log(object);
        });
        logger.info("Logging consumming..");
        consumerLogger.consume();
    }
}
