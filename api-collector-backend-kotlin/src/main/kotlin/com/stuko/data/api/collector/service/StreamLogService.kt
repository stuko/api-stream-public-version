package com.stuko.data.api.collector.service

import org.springframework.stereotype.Component

@Component
interface StreamLogService {
    fun list(): MutableList<MutableMap<String,Any>>?
    fun list(collection : String): MutableList<MutableMap<String,Any>>?
}