package com.stuko.stream.api.topology

import com.stuko.stream.api.module.IModule

interface ITopology {
    var topologyId : String
    var topologyName : String
    fun addBoltModule(module:IModule):Unit
    fun addSpoutModule(module:IModule):Unit
    fun connectModule(from:String, to:String, name:String, condition:String, kind:IModule.KIND):Unit
    fun executeModules(): Unit
    fun stopModules(): Unit
}