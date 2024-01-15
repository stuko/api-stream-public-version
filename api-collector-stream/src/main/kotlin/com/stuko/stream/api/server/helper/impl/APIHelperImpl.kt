package com.stuko.stream.api.server.helper.impl

import com.stuko.stream.api.server.helper.APIHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class APIHelperImpl : APIHelper, Serializable, MongoDBHelperImpl() {

    override fun getAPIInfo(key: String): MutableList<MutableMap<String, Any>> {
        var p : MutableMap<String,Any> = mutableMapOf()
        p.put("apiId",key)
        return this.help("TopologyGroups","API", p)
    }

    override fun getCertList(brokerServer:String, numOfPartition:String, replicationFactor:String, list: MutableList<MutableMap<String, Any>>): MutableList<MutableMap<String, Any>> {
        var certList:MutableList<MutableMap<String, Any>> = mutableListOf()
        if(list != null && list.size == 1){
            if(list[0]["cert"] != null){
                var cKeyList :MutableList<MutableMap<String,Any>>  = list[0]["cert"] as MutableList<MutableMap<String,Any>>
                cKeyList.forEach { m ->
                    val cList:MutableList<MutableMap<String,Any>> = this.help("TopologyGroups","CERT", m)
                    cList.forEach { cm ->
                        var cMap : MutableMap<String,Any> = mutableMapOf()
                        cm.forEach { t, u ->
                            cMap.put(t,u)
                        }
                        var period = ((cm["period"] ?: "60000") as String) ?: "60000"
                        var percent = ((cm["percent"] ?: "100") as String) ?: "100"
                        var wait = ((cm["wait"] ?: "100") as String) ?: "100"
                        certList.add(cMap)
                    }
                }
            }
        }
        return certList
    }
}