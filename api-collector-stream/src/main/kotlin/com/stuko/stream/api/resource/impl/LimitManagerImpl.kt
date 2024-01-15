package com.stuko.stream.api.resource.impl

import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.resource.LimitManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.text.SimpleDateFormat
import java.util.*

@Configuration
class LimitManagerConfig{
    @Bean(name=["LimitManager"])
    fun getLimitManager(@Qualifier("MongoDAO") dao:MongoDAO) : LimitManager{
        return LimitManagerImpl(dao)
    }
}

class LimitManagerImpl(var dao : MongoDAO) : LimitManager {
    companion object : Log
    var sdf: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS")
    override fun isAccessible(serverIp:String, topicName: String, moduleName : String, limit : Int, period: Long): Boolean {
        logger.info("---------- check accessible --------------")
        logger.info("serverIp : {} ", serverIp)
        logger.info("topicName : {} ", topicName)
        logger.info("---------- check accessible --------------")
        var parameter:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var andMap:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()

        var timeBetweenMap:MutableMap<String,Any> = mutableMapOf<String, Any>()
        var cur:Long = System.currentTimeMillis()
        var ltVar = sdf.format(Date(cur))
        var gtVar = sdf.format(Date(cur-period))
        logger.info("isAccessible from : $gtVar")
        logger.info("isAccessible to : $ltVar")

        timeBetweenMap["\$lt"] = ltVar
        timeBetweenMap["\$gt"] = gtVar

        andMap.add(mutableMapOf<String, Any>())
        andMap.get(andMap.size-1)["topology.topic.module.moduleName"] = moduleName
        andMap.add(mutableMapOf<String, Any>())
        andMap.get(andMap.size-1)["topology.topic.module.log.result.certId"] = topicName
        andMap.add(mutableMapOf<String, Any>())
        andMap.get(andMap.size-1)["topology.topic.module.log.result.Retry"] = "false"
        andMap.add(mutableMapOf<String, Any>())
        andMap.get(andMap.size-1)["topology.topic.module.log.result.BoltTxTime"] = timeBetweenMap

        parameter["\$and"] = andMap

        logger.info("isAccessible parameter : {}" , parameter )

        val curCount = dao!!.find("TopologyGroups","LOG",parameter).count()

        logger.info("isAccessible cur count : {}" , curCount)

        return if(curCount > limit){
            logger.info("isAccessible  is over cur count : {} , limit : {} , period : {}" , curCount , limit, period)
            false
        }else{
            logger.info("isAccessible  is not over cur count : {} , limit : {} , period : {}" , curCount , limit, period)
            true
        };

        // Deprecated..........
        // return KafkaUtils.checkLimit(serverIp,  topicName )
    }

    override fun startLimitCheck(serverIp:String, topicName:String, partition:Int, replication:Int, period: Int, percent: Int, limit: Long , wait:Long){
        // Deprecated...........
        // KafkaUtils.createTopic(KafkaUtils.getProperties("admin", serverIp , true), topicName, partition, replication.toShort())
        // KafkaUtils.checkPeriod(serverIp, KafkaUtils.getConsumer(false , serverIp, topicName , true), topicName, period , percent,  limit , wait)
    }
}