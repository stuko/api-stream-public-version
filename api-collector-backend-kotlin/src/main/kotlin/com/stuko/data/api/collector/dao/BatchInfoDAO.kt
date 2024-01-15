package com.stuko.data.api.collector.dao

import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface BatchInfoDAO {
 fun read(param : Map<String,Any>):Map<String,Any>
 fun create(param : Map<String,Any>):Map<String,Any>
 fun update(param : Map<String,Any>):Map<String,Any>
 fun delete(param : Map<String,Any>):Map<String,Any>
 fun list(page: Int):List<Map<String,Any>>
}