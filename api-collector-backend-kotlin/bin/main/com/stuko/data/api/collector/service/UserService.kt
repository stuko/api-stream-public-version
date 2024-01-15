package com.stuko.data.api.collector.service

import com.stuko.data.api.collector.vo.UserVO
import org.springframework.stereotype.Service

interface UserService {
    fun read(param : String):Map<String,Any>
    fun create(param : UserVO):Int
    fun update(param : UserVO):Int
    fun delete(param : String):Int
    fun login(id: String, pw: String):String?
}