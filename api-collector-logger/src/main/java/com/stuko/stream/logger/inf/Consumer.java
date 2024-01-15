package com.stuko.stream.logger.inf;

public interface Consumer {
    void init(KafkaConsumerAction action);
    void stop();
    void consume();
}
