package com.stuko.stream.api.server.helper

import java.io.Serializable

interface APIHelper : Serializable {
    fun getAPIInfo(key : String) :MutableList<MutableMap<String,Any>>
    fun getCertList(brokerServer:String, numOfPartition:String, replicationFactor:String, list: MutableList<MutableMap<String, Any>>) :MutableList<MutableMap<String,Any>>
}