package com.stuko.stream.api.resource

import java.io.Serializable

interface LimitManager : Serializable{
  fun startLimitCheck(serverIp:String, topicName:String, partition:Int, replication:Int, period: Int, percent: Int, limit: Long , wait: Long)
  fun isAccessible(serverIp:String, topicName: String, moduleName : String, limit : Int, period: Long): Boolean
}