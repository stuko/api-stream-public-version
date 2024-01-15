package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.MapUtils
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector

class TransformModule (moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                       logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    var variables:String = ""
    var array:List<String>? = null
    var map:MutableMap<String,String>? = null

    override fun execute(param: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.info("----------- TransformModule ------------")
        logger.info("TransformModule is called to {}" , map)
        logger.info("TransformModule is called by {}" , param)
        logger.info("----------- TransformModule ------------")
        this.log(param,null)
        if(this.map != null && this.map!!.isNotEmpty()) {
            this.map?.forEach { (k, v) ->
                param?.set(k, MapUtils.getJSONData(v,param))
            }
        }
        this.log(null, param)
        return param
    }

    override fun afterPrepare(collector: OutputCollector?) {
    }

    override fun start():Boolean {
        variables = this.getProperty("variables")
        array = this.variables.split(",")
        map = mutableMapOf()
        for(one:String in array!!){
            var items:List<String> = one.split("=")
            map!![items[0]] = items[1]
        }
        return true
    }

    override fun cleanup() {
    }

}