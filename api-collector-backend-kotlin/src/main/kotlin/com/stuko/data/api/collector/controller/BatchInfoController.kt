package com.stuko.data.api.collector.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.data.api.collector.service.BatchInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/apicert")

class BatchInfoController {

    val gson = Gson()

    @Autowired
    lateinit var service : BatchInfoService

    @GetMapping("/hello")
    fun hello() :String {
        // var ho:String = "hi hello"
        return "hi hello............"
    }

    @RequestMapping(path=["/createBatch"], method = [RequestMethod.POST], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createBatch(httpEntity: HttpEntity<String>) :String {
        val map: Map<String, Any> = getParameter(httpEntity)
        return getResponseJson(service.create(map))
    }

    @RequestMapping(path=["/updateBatch"], method = [RequestMethod.POST], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateBatch(httpEntity: HttpEntity<String>) :String {
        val map: Map<String, Any> = getParameter(httpEntity)
        return getResponseJson(service.update(map))
    }

    @RequestMapping(path=["/deleteBatch"], method = [RequestMethod.POST], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun deleteBatch(httpEntity: HttpEntity<String>) :String {
        val map: Map<String, Any> = getParameter(httpEntity)
        return getResponseJson(service.delete(map))
    }

    @RequestMapping(path=["/readBatch"], method = [RequestMethod.POST], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun readBatch(httpEntity: HttpEntity<String>) :String {
        val map: Map<String, Any> = getParameter(httpEntity)
        return getResponseJson(service.read(map))
    }

    @RequestMapping(path=["/listBatch"], method = [RequestMethod.POST], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun listBatch(httpEntity: HttpEntity<String>) :String {
        val map: Map<String, Any> = getParameter(httpEntity)
        return getResponseListJson(service.list(map.get("pos") as Int))
    }

    private fun getParameter(httpEntity: HttpEntity<String>): Map<String, Any> {
        val json: String? = httpEntity.getBody();
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, mapType)
        return map
    }

    /*
    DAO 결과를 JSON으로 리턴해주기 위해서는
    해당 결과를 구분이 가능한 이름으로 Wrapping 해줘야 함
    Wrapping 구조는 Map 구조로 해줘야 함.

    example)
       Map<String,String> : read 결과
         --> Map<"read",Map<String,String>>
       List<Map<String,String>> : list 결과
         --> Map<"list",List<Map<String,String>>>
     */
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

}