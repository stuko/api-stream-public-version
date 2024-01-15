package com.stuko.data.api.collector.dao

import com.stuko.data.api.collector.vo.UserVO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Component

@Mapper
@Component
interface UserDAO {
    fun read(param : String):Map<String,Any>
    fun create(param : UserVO):Int
    fun update(param : UserVO):Int
    fun delete(param : String):Int
}