package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.dao.CommonMongoDAO
import com.stuko.data.api.collector.service.CertInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CertInfoServiceImpl : CertInfoService {

    @Autowired
    private lateinit var mongo : CommonMongoDAO

    override fun create(param: Map<String, Any>): String {
        mongo.insert("TopologyGroups","CERT", param)
        return "true"
    }

    override fun update(param: Map<String, Any>): String {
        var search:MutableMap<String,Any> = mutableMapOf()
        search["certId"] = param["certId"] as String
        mongo.update("TopologyGroups","CERT", search, param)
        return "true"
    }

    override fun delete(param: Map<String, Any>): String {
        var searchMap : MutableMap<String, Any> = mutableMapOf()
        searchMap["cert"] = param
        var findMap : MutableMap<String, Any> = mongo.findOne("TopologyGroups","CERT", searchMap)
        if (findMap.isEmpty()) {
            mongo.delete("TopologyGroups", "CERT", param)
            return "true"
        }else{
            return "false"
        }
    }

    override fun readAll(): MutableList<MutableMap<String, Any>>? {
        var apiList: MutableList<MutableMap<String, Any>>
        apiList = mongo.getCollections("TopologyGroups" , "CERT")!!
        return apiList
    }
}