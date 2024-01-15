package com.stuko.stream.api.dao.impl

import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.LogDAO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LogDAOConfig{
    @Bean(name=["LogDAO"])
    fun getLogDAO(@Qualifier("MongoDAO") dao: MongoDAO ) : LogDAO{
        return LogDAOImpl(dao)
    }
}

class LogDAOImpl(var dao : MongoDAO) : LogDAO{
    companion object : Log
    override fun create(db :String, collection:String, batch:String, topologyId:String, topicName:String, moduleType: String, moduleName:String, param: MutableMap<String, Any>): MutableMap<String, Any> {
        logger.debug("#### MongoDB logging... to LOG table : $topologyId")
        var batchMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var topologyMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var topicMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var moduleMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        moduleMap["moduleName"] = moduleName
        moduleMap["moduleType"] = moduleType
        moduleMap["log"] = param
        topicMap["topicName"] = topicName
        topicMap["module"] = moduleMap
        topologyMap["topologyId"] = topologyId
        topologyMap["topic"] = topicMap
        batchMap["batchId"] = batch
        batchMap["topology"] = topologyMap
        logger.debug("##### DAO will save log....")
        dao.insert(db,collection,batchMap)
        return batchMap;
    }
}