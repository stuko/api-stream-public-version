package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.module.IModule
import org.apache.storm.spout.SpoutOutputCollector

class TcpModule(moduleType: IModule.KIND, moduleKey:String, moduleCount: Int,
                logBufferManager: AbstractLogBufferManager
) : AbstractSpoutModule(moduleType,moduleKey, moduleCount, logBufferManager) {
    companion object : Log
    override fun start():Boolean{return true}
    override fun execute(map: MutableMap<String, Any>?, collector: SpoutOutputCollector?): MutableMap<String, Any>? {
        logger.info("--------- TcpModule ------------")
        logger.info("########### Input Data  : {} " , map)
        logger.info("--------- TcpModule ------------")
        return map
    }
}