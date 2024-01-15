package com.stuko.data.api.collector.service

import org.springframework.stereotype.Component

@Component
interface TopologyService {
  fun read(param : Map<String,Any>):MutableList<MutableMap<String,Any>>
  fun create(param : Map<String,Any>):Map<String,Any>
  fun update(param : Map<String,Any>):Map<String,Any>
  fun delete(param : Map<String,Any>):Map<String,Any>
  fun list(): MutableList<MutableMap<String,Any>>?
  fun list(collection : String): MutableList<MutableMap<String,Any>>?
}