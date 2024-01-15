package com.stuko.stream.logger.inf;

import java.util.Properties;

public interface PropertyFactory {
    public static enum Type { ADMIN, CONSUMER, PRODUCER}
    Properties getKafkaProperties(Type type);
}
