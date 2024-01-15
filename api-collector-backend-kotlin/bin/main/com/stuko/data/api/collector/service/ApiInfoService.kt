package com.stuko.data.api.collector.service

interface ApiInfoService {
    fun create(param : Map<String, Any>):String
    fun update(param : Map<String, Any>):String
    fun delete(param : Map<String, Any>):String
    fun readAll():MutableList<MutableMap<String, Any>>?
}