package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.module.IModule
import com.stuko.stream.api.module.impl.AbstractSpoutModule
import org.apache.storm.spout.SpoutOutputCollector

class StartModule(moduleType: IModule.KIND, moduleKey:String, moduleCount: Int,
                  logBufferManager: AbstractLogBufferManager
) : AbstractSpoutModule(moduleType,moduleKey, moduleCount, logBufferManager) {
    companion object : Log
    override fun start():Boolean{return true}
    override fun execute(map: MutableMap<String, Any>?, collector: SpoutOutputCollector?): MutableMap<String, Any>? {
        logger.info("--------- StartModule ------------")
        logger.info("########### Input Data  : {} " , map)
        logger.info("--------- StartModule ------------")
        return map
    }
}

