package com.stuko.stream.api.dao

import com.stuko.stream.api.module.IModule
import java.io.Serializable

// @Mapper
// @Component
interface LogDAO : Serializable{
    fun create(db :String, collection:String, batch:String, topologyId:String, topicName:String, moduleType: String, moduleName:String, param : MutableMap<String,Any>):MutableMap<String,Any>
}