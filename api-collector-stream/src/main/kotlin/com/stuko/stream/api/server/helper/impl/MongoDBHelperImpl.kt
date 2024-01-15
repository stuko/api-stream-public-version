package com.stuko.stream.api.server.helper.impl

import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.server.helper.Helper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MongoDBHelperImpl: Helper {

    @Autowired
    lateinit var dao:MongoDAO
    override fun help(collection: String, schema:String, param: MutableMap<String, Any>): MutableList<MutableMap<String, Any>> {
        return dao.find(collection,schema,param)
    }
}