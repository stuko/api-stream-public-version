package com.stuko.data.api.collector.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.data.api.collector.common.Log
import com.stuko.data.api.collector.service.ApiInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/apicert")
class ApiInfoController {

    companion object : Log
    val gson = Gson()
    @Autowired
    lateinit var service : ApiInfoService

    @RequestMapping(path=["/createApi"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var apiInfoMap:MutableMap<String,Any> = mutableMapOf()
//            apiInfoMap["apiId"] = "API_" + System.nanoTime()
            apiInfoMap["apiId"] = param!!["apiId"] as String
            apiInfoMap["apiName"] = param!!["apiName"] as String
            apiInfoMap["url"] = param!!["url"] as String
            apiInfoMap["cert"] = param!!["cert"] as MutableList<MutableMap<String, String>>
            apiInfoMap["output_format"] = param!!["output_format"] as String
            apiInfoMap["chgr_no"] = session.getAttribute("id") as String
            apiInfoMap["chg_dt_tm"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

            val result : String = service.create(apiInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/updateApi"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var apiInfoMap:MutableMap<String,Any> = mutableMapOf()
            apiInfoMap["apiId"] = param!!["apiId"] as String
            apiInfoMap["apiName"] = param!!["apiName"] as String
            apiInfoMap["url"] = param!!["url"] as String
            apiInfoMap["cert"] = param!!["cert"] as MutableList<MutableMap<String, String>>
            apiInfoMap["output_format"] = param!!["output_format"] as String
            apiInfoMap["chgr_no"] = session.getAttribute("id") as String
            apiInfoMap["chg_dt_tm"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

            val result : String = service.update(apiInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/deleteApi"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun deleteApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var apiInfoMap:MutableMap<String,String> = mutableMapOf()
            apiInfoMap["apiId"] = param!!["apiId"] as String
            val result : String = service.delete(apiInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/readAllApi"], method = [RequestMethod.GET])
    @ResponseBody
    fun readAllApi() :String {
        return getResponseListJson(service.readAll())
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

    fun getResponseListJson(list: List<Map<String,Any>>?) : String{
        return if(list == null){
            gson.toJson(listOf(mapOf("-1" to "CODE")))
        }else{
            gson.toJson(list)
        }
    }
}
