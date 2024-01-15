package com.stuko.stream.api.module.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.module.IModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.storm.task.OutputCollector
import org.apache.storm.topology.BasicOutputCollector
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseBasicBolt
import org.apache.storm.tuple.Fields
import org.apache.storm.tuple.Tuple
import java.util.*

class EndModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {
    companion object : Log
    override fun start():Boolean{
        return true
    }
    override fun afterPrepare(collector: OutputCollector?): Unit{
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        // this.log(map, null)
        logger.info("----------- EndModule ------------")
        logger.info("EndModule is called {}" , map)
        logger.info("----------- EndModule ------------")
        this.log(null, map)
        this.async = true
        return map
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }

}