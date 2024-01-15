package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.dao.CommonMongoDAO
import com.stuko.data.api.collector.service.TopologyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TopologyServiceImpl : TopologyService{

    @Autowired
    private lateinit var mongo : CommonMongoDAO

    override fun read(param: Map<String, Any>): MutableList<MutableMap<String,Any>> {
        var name:String = param.get("topologyGroup")as String
        return mongo.find("TopologyGroups",name, param)
    }

    override fun create(param: Map<String, Any>): Map<String, Any> {
        var name:String = param.get("topologyGroup")as String
        mongo.insert("TopologyGroups",name, param)
        return param
    }

    override fun update(param: Map<String, Any>): Map<String, Any> {
        var name:String = param.get("topologyGroup")as String
        var search:MutableMap<String,Any> = mutableMapOf()
        search.put("topologyId", param.get("topologyId")as String)
        mongo.update("TopologyGroups",name, search, param)
        return param
    }

    override fun delete(param: Map<String, Any>): Map<String, Any> {
        var name:String = param.get("topologyGroup")as String
        mongo.delete("TopologyGroups",name, param)
        return param
    }

    override fun list(): MutableList<MutableMap<String, Any>>? {
        return mongo.getCollections("TopologyGroups")
    }

    override fun list(collection : String): MutableList<MutableMap<String, Any>>? {
        return mongo.getCollections("TopologyGroups" , collection)
    }

}