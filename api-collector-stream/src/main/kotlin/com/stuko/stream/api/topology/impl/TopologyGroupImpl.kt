package com.stuko.stream.api.topology.impl

import com.stuko.stream.api.topology.ITopology
import com.stuko.stream.api.topology.ITopologyGroup

class TopologyGroupImpl: ITopologyGroup {

    private var topologyGroupName : String = ""
    private var topologyMap:MutableMap<String,ITopology> = mutableMapOf()

    override fun setTopologyGroupName(name:String): Unit{
        this.topologyGroupName = name
    }

    override fun getTopologyGroupName(): String {
        return this.topologyGroupName
    }

    override fun runTopology(topologyName: String) {
       this.getTopologyMap()[topologyName]?.executeModules()
    }

    override fun stopTopology(topologyName: String) {
        this.getTopologyMap()[topologyName]?.stopModules();
    }

    override fun runAllTopology() {
        this.getTopologyMap().forEach{ (k, t) ->
            t.executeModules()
        }
    }

    override fun stopAllTopology() {
        this.getTopologyMap().forEach{ (k, t) ->
            t.stopModules()
        }
    }

    override fun getTopologyMap(): MutableMap<String, ITopology> {
        return this.topologyMap
    }
}