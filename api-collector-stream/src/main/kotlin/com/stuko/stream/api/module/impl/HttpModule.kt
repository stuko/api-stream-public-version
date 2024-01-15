package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.module.IModule
import com.stuko.stream.api.module.vertex.RestServer
import org.apache.storm.spout.SpoutOutputCollector
import org.apache.storm.tuple.Values

class HttpModule(moduleType: IModule.KIND, moduleKey:String, moduleCount: Int,
                 logBufferManager: AbstractLogBufferManager
) : AbstractSpoutModule(moduleType,moduleKey, moduleCount, logBufferManager) {
    companion object : Log
    var port:String = "9999"
    override fun start():Boolean{
        this.getProperty().forEach {m->
            val name:String = m.get("name") as String;
            val value:String = m.get("value") as String;
            if("port".equals(name)) this.port = value;
            if("producerTopic".equals(name)) this.producerTopic = value;
        }
        logger.info("HttpModule will be started...")
        logger.info("producerTopic is {} ", this.producerTopic)
        RestServer().start(this, this.port.toInt(),this.producerTopic,this.kafkaProducerProps)
        logger.info("RestServer is Listining : {}", this.port)
        return true
    }
    override fun execute(map: MutableMap<String, Any>?, collector: SpoutOutputCollector?): MutableMap<String, Any>? {
        logger.info("--------- HttpModule ------------")
        logger.info("########### Input Data  : {} " , map)
        logger.info("--------- HttpModule ------------")
        return map
    }
}