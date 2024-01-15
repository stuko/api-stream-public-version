package com.stuko.data.api.collector.service

import org.springframework.stereotype.Component

@Component
interface BatchInfoService {
    fun read(param : Map<String,Any>):Map<String,Any>
    fun create(param : Map<String,Any>):Map<String,Any>
    fun update(param : Map<String,Any>):Map<String,Any>
    fun delete(param : Map<String,Any>):Map<String,Any>
    fun list(page : Int): List<Map<String,Any>>
}