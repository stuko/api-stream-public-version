package com.stuko.stream.api.topology

import com.stuko.stream.api.topology.ITopology

interface ITopologyGroup {
    fun setTopologyGroupName(name:String): Unit
    fun getTopologyGroupName(): String
    fun runTopology(topologyName: String): Unit
    fun stopTopology(topologyName: String): Unit
    fun runAllTopology(): Unit
    fun stopAllTopology(): Unit
    fun getTopologyMap(): MutableMap<String, ITopology>
}