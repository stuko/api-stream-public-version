package com.stuko.stream.api.server

import com.stuko.stream.api.topology.ITopologyGroup

interface IStreamServer {
    var topologyGroupName:String
    fun start(topologyGroupName: String): Unit
    fun stop(): Unit
    fun status(): MutableMap<String, Any>
    fun getTopologyGroup(topologyGroupName: String): ITopologyGroup?
}