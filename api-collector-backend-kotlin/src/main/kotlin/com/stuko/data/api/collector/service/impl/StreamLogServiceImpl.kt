package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.dao.CommonMongoDAO
import com.stuko.data.api.collector.service.StreamLogService
import com.stuko.data.api.collector.service.TopologyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StreamLogServiceImpl : StreamLogService {

    @Autowired
    private lateinit var mongo : CommonMongoDAO

    override fun list(): MutableList<MutableMap<String, Any>>? {
        return mongo.getCollections("Log")
    }

    override fun list(collection : String): MutableList<MutableMap<String, Any>>? {
        return mongo.getCollections("Log" , collection)
    }

}