package com.stuko.stream.logger.inf;

@FunctionalInterface
public interface KafkaConsumerAction {
    void act(Object parameter);
}
