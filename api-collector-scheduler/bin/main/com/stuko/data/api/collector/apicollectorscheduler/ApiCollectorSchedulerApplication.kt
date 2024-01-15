package com.stuko.data.api.collector.apicollectorscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class ApiCollectorSchedulerApplication

fun main(args: Array<String>) {
    runApplication<ApiCollectorSchedulerApplication>(*args)
}
