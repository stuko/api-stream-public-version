package com.stuko.data.api.collector.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Log {
    val logger: Logger get() = LoggerFactory.getLogger(this.javaClass)
}