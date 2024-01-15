package com.stuko.stream.api.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.stream.api.buffer.LogBufferManagerConfig
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.resource.LimitManager
import com.stuko.stream.api.resource.impl.LimitManagerImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.tags.Param
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/stream/")
class StreamController {
    var logger: Logger = LoggerFactory.getLogger(StreamController::class.java)
    @Autowired
    lateinit var dao : MongoDAO
    var sdf: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS")

    val gson = Gson()

    @RequestMapping(path = ["/start"], method = [RequestMethod.POST, RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTopologyList(httpEntity: HttpEntity<String>): String {
       val map: Map<String, Any>? = getParameter(httpEntity)
       return getResponseListJson(null)
    }

    @RequestMapping(path = ["/checkLimit"], method = [RequestMethod.POST, RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun checkLimit(@RequestParam("serverIp") serverIp :String
                 , @RequestParam("topicName") topicName :String
                 , @RequestParam("moduleName") moduleName :String
                 , @RequestParam("limit") limit :String
                 , @RequestParam("period") period :String): String {
        val result : Boolean =  this.checkLimit(serverIp , topicName, moduleName, limit.toInt(), period.toLong() )
        var resultMap = mutableMapOf<String,Any>()
        resultMap["result"] = result.toString()
        return this.getResponseJson(resultMap)
    }

    private fun getParameter(httpEntity: HttpEntity<String>?): MutableMap<String, Any>? {
        val json: String? = httpEntity?.getBody();
        println("-----------------------------")
        println(json)
        println("-----------------------------")
        if(json == null) return null
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val map: MutableMap<String, Any> = gson.fromJson(json, mapType)
        return map
    }
    fun getResponseJson(map: Map<String,Any>?) : String{
        if(map == null){
            return gson.toJson(mapOf("-1" to "CODE"))
        }else{
            return gson.toJson(map)
        }
    }

    fun getResponseListJson(list: List<Map<String,Any>>?) : String{
        if(list == null){
            return gson.toJson(listOf(mapOf("-1" to "CODE")))
        }else{
            return gson.toJson(list)
        }
    }

    private fun checkLimit(serverIp:String, topicName: String, moduleName : String, limit : Int, period: Long) : Boolean{
        logger.info("---------- check accessible --------------")
        logger.info("serverIp : {} ", serverIp)
        logger.info("topicName : {} ", topicName)
        logger.info("---------- check accessible --------------")
        var parameter:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var andMap:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()

        var timeBetweenMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var cur:Long = System.currentTimeMillis()
        var ltVar = sdf.format(Date(cur))
        var gtVar = sdf.format(Date(cur-period))
        logger.info("isAccessible from : $gtVar")
        logger.info("isAccessible to : $ltVar")

        timeBetweenMap["\$lt"] = ltVar
        timeBetweenMap["\$gt"] = gtVar

        andMap.add(mutableMapOf<String, Any>())
        andMap[andMap.size-1]["topology.topic.module.moduleName"] = moduleName
        andMap.add(mutableMapOf<String, Any>())
        andMap[andMap.size-1]["topology.topic.module.log.result.certId"] = topicName
        andMap.add(mutableMapOf<String, Any>())
        andMap[andMap.size-1]["topology.topic.module.log.result.Retry"] = "false"
        andMap.add(mutableMapOf<String, Any>())
        andMap[andMap.size-1]["topology.topic.module.log.result.BoltTxTime"] = timeBetweenMap

        parameter["\$and"] = andMap

        logger.info("isAccessible parameter : {}" , parameter )

        val curCount = dao!!.find("TopologyGroups",topicName,parameter).count()

        logger.info("isAccessible cur count : {}" , curCount)

        return if(curCount > limit){
            logger.info("isAccessible  is over cur count : {} , limit : {} , period : {}" , curCount , limit, period)
            false
        }else{
            logger.info("isAccessible  is not over cur count : {} , limit : {} , period : {}" , curCount , limit, period)
            true
        };
    }
}