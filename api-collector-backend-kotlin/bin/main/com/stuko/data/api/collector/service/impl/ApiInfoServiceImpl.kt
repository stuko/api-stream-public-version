package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.dao.CommonMongoDAO
import com.stuko.data.api.collector.service.ApiInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApiInfoServiceImpl : ApiInfoService {

    @Autowired
    private lateinit var mongo : CommonMongoDAO

    override fun create(param: Map<String, Any>): String {
        mongo.insert("TopologyGroups","API", param)
        return "true"
    }

    override fun update(param: Map<String, Any>): String {
        var search:MutableMap<String,Any> = mutableMapOf()
        search["apiId"] = param["apiId"] as String
        mongo.update("TopologyGroups","API", search, param)
        return "true"
    }

    override fun delete(param: Map<String, Any>): String {
        mongo.delete("TopologyGroups","API", param)
        return "true"
    }

    override fun readAll(): MutableList<MutableMap<String, Any>>? {
        var apiList: MutableList<MutableMap<String, Any>>
        apiList = mongo.getCollections("TopologyGroups" , "API")!!
        return apiList
    }
}