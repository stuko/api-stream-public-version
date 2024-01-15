package com.stuko.data.api.collector.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hazelcast.config.NearCacheConfig
import com.hazelcast.core.HazelcastInstance
import com.stuko.data.api.collector.common.Log
import com.stuko.data.api.collector.service.UserService
import com.stuko.data.api.collector.vo.UserVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession


@RestController
@RequestMapping("/apicert")
class UserController {

    companion object : Log
    val gson = Gson()
    @Autowired
    lateinit var service : UserService

    @RequestMapping(path=["/createUser"], method = [RequestMethod.POST])
    @ResponseBody
    fun createUser(user: UserVO) :String {
        // application code
        return "createUser"
    }

    @RequestMapping(path=["/updateUser"], method = [RequestMethod.POST])
    @ResponseBody
    fun updateUser(user: UserVO) :String {
        // application code
        return "updateUser"
    }

    @RequestMapping(path=["/deleteUser"], method = [RequestMethod.POST])
    @ResponseBody
    fun deleteUser(id : String) :String {
        // application code
        return "deleteUser"
    }

    @RequestMapping(path=["/readUser"], method = [RequestMethod.GET])
    @ResponseBody
    fun readUser(id : String) :String {
        // application code
        return "readUser"
    }

    @RequestMapping(path=["/login"] , method = [RequestMethod.POST, RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun login(session:HttpSession, httpEntity: HttpEntity<String>) :String {
        val param: Map<String, Any>? = getParameter(httpEntity)
        val id : String = param?.get("id") as String ?: ""
        val pw : String = param?.get("pw") as String ?: ""
        val result : String? = service.login(id,pw)
        var map:MutableMap<String,String> = mutableMapOf()
        if(result != null) {
            map.put("result", "true")
            session.setAttribute("id",id)
        }else {
            map.put("result", "false")
        }
        return gson.toJson(map)
    }
    @RequestMapping(path=["/checkLogin"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun checkLogin(session:HttpSession) :String {
        var map: MutableMap<String, String> = mutableMapOf()
        if(session != null && session.getAttribute("id") != null) {
            val id: String? = session.getAttribute("id") as String ?: ""
            logger.info("############## Session Check {} ############", id)
            if (id != null) {
                map.put("result", "true")
            } else {
                map.put("result", "false")
            }
        }else{
            map.put("result", "false")
        }
        return gson.toJson(map)
    }

    @RequestMapping(path=["/logOut"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun logOut(session:HttpSession) :String {
        var map: MutableMap<String, String> = mutableMapOf()
        if(session != null && session.getAttribute("id") != null) {
            session.invalidate();
            map.put("result", "true")
        }else{
            map.put("result", "false")
        }
        return gson.toJson(map)
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
}