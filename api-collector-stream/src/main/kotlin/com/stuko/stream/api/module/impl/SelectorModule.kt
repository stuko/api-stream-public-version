package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.MapUtils
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values

class SelectorModule(moduleType: IModule.KIND, moduleKey: String, moduleCount :Int, logBufferManager: AbstractLogBufferManager)
    : AbstractBoltModule(moduleType,moduleKey,moduleCount,logBufferManager) {

    companion object : Log

    private var selector : String? = ""

    override fun start():Boolean{
        this.selector = this.getProperty("select") ?: ""
        return true
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.debug("----------- SelectorModule ------------")
        logger.debug("######## SelectorModule's parameter is {}", map)
        logger.debug("----------- SelectorModule ------------")
        this.async = true
        try {
            if (this.selector != null && this.selector!!.trim().isNotEmpty()) {
                var resultMap:MutableMap<String,Any>? = mutableMapOf();
                logger.info("selector is {}", selector)
                var keys: List<String> = selector!!.trim().split(",")
                for (key: String in keys) {
                    logger.info("selector's key is {}", key)
                    var value: String = getJSONData(key,map)
                    logger.info("selector's value is {}", value)
                    resultMap?.put(key.replace(".","_"),value)
                }
                if(collector == null) {
                    logger.info("Selector's collector is NULL!!!!!!!!!!!!")
                } else {
                    resultMap?.put("SELECTOR", selector!!)
                    collector?.emit(Values(resultMap))
                    logger.info("Selector's collector emit!!!!!!!!!!!!")
                    this.log(null, resultMap, true)
                    logger.info("Selector's logging... complete!!!!!!!!!!!! {}", resultMap)
                }
                return resultMap
            }else{
                map?.put("SELECTOR", "NULL")
                collector?.emit(Values(map))
                this.log(null, map)
                return map
            }
        }catch(e:Exception){
            logger.error(e.toString())
            if (collector != null) {
                map?.put("ERROR",e.toString())
                collector.emit(Values(map))
                this.log(null, map)
            }
        }
        return map;
    }

    private fun getJSONData(key:String, map:MutableMap<String,Any>?):String{
        return MapUtils.getJSONData(key,map)
    }

    override fun afterPrepare(collector: OutputCollector?) {
    }

    override fun cleanup() {
    }

}