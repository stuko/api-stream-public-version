package com.stuko.data.api.collector.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.data.api.collector.common.Log
import com.stuko.data.api.collector.service.CertInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/apicert")
class CertInfoController {
    companion object : Log
    val gson = Gson()
    @Autowired
    lateinit var service : CertInfoService

    @RequestMapping(path=["/createCert"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var certInfoMap:MutableMap<String,String> = mutableMapOf()
            certInfoMap["certId"] = param!!["certId"] as String
            certInfoMap["siteName"] = param!!["siteName"] as String
            certInfoMap["certKey"] = param!!["certKey"] as String
            certInfoMap["limit"] = param!!["limit"] as String
            certInfoMap["expired"] = param!!["expired"] as String
            certInfoMap["chgr_no"] = session.getAttribute("id") as String
            certInfoMap["chg_dt_tm"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

            val result : String = service.create(certInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/updateCert"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var certInfoMap:MutableMap<String,String> = mutableMapOf()
            certInfoMap["certId"] = param!!["certId"] as String
            certInfoMap["siteName"] = param!!["siteName"] as String
            certInfoMap["certKey"] = param!!["certKey"] as String
            certInfoMap["limit"] = param!!["limit"] as String
            certInfoMap["expired"] = param!!["expired"] as String
            certInfoMap["chgr_no"] = session.getAttribute("id") as String
            certInfoMap["chg_dt_tm"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

            val result : String = service.update(certInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/deleteCert"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun deleteApi(session: HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        var responseMap:MutableMap<String,String> = mutableMapOf()

        if (session?.getAttribute("id") != null){
            var certInfoMap:MutableMap<String,String> = mutableMapOf()
            certInfoMap["certId"] = param!!["certId"] as String
            val result : String = service.delete(certInfoMap)
            responseMap["result"] = result
        }else{
            responseMap["result"] = "false"
        }
        return gson.toJson(responseMap)
    }

    @RequestMapping(path=["/readAllCert"], method = [RequestMethod.GET])
    @ResponseBody
    fun readAllCert() :String {
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