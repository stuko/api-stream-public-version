package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values
import java.util.*

class RangeModule (moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                   logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    var rangeName:String = ""
    var rangeList:List<String> = mutableListOf()
    var isNumber:Boolean = true

    public override fun execute(param: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.info("----------- RangeModule ------------")
        logger.info("RangeModule is called ")
        logger.info("----------- RangeModule ------------")
        try {
            this.log(param, null)
            if (param != null) {
                for (item in rangeList) {
                    var tmpData = getRangeRandomValue(item)
                    if (rangeName != null || rangeName.isNotEmpty()) {
                        var map: MutableMap<String, Any> = mutableMapOf()
                        map.putAll(param)
                        map[rangeName] = tmpData
                        collector?.emit(Values(map))
                        this.log(param, map)
                    }
                }
            }
        }catch(e:Exception){
            val ste: Array<StackTraceElement> = e.stackTrace
            val className = ste[0].className
            val methodName = ste[0].methodName
            val lineNumber = ste[0].lineNumber
            val fileName = ste[0].fileName
            logger.error(e.toString(),e)
            if (collector != null) {
                param?.put("ERROR",
                    "$e :  class = $className, methodName = $methodName , lineNumber = $lineNumber , fileName = $fileName"
                )
                collector.emit(Values(param))
                this.log(null, param)
            }
        }finally{
            this.async = true
        }
        return param
    }

    public fun getValue(data:String):List<String>{
        var tmpData = data.trim()
        if(tmpData.indexOf("~") >= 0){
            var r:List<String> = tmpData.split("~")
            isNumber = true
            // double형
            return r
        }else if(tmpData.indexOf(":") >= 0){
            var r:List<String> = tmpData.split(":")
            isNumber = false
            return r
        }else {
            isNumber = false
            return listOf(tmpData)
        }
    }

    public fun getRangeRandomValue (data:String):String{
        var tmpData = data.trim()
        if(tmpData.indexOf("~") >= 0){
            var r:List<String> = tmpData.split("~")
            // double형
            if(r[0].trim().indexOf(".") >= 0){
                var start = r[0].trim().toDouble()
                var end = r[1].trim().toDouble()
                var result = Math.random()*(end - start + 1) + start
                return result.toString()
            }else{
                var start = r[0].trim().toInt()
                var end = r[1].trim().toInt()
                var result = (Math.random()*(end - start + 1)).toInt() + start
                return result.toString()
            }
        }else if(tmpData.indexOf(":") >= 0){
            var r:List<String> = tmpData.split(":")
            return r[(Math.random()*(r.size)).toInt()].toString()
        }else return tmpData
    }

    override fun afterPrepare(collector: OutputCollector?) {
    }

    override fun start() :Boolean{
        logger.info("RangeModule is starting.....")
        rangeName = this.getProperty("name").trim()
        logger.info("RangeModule's rangeName is $rangeName")
        var range = this.getProperty("range")
        logger.info("RangeModule's range is $range")
        if(range is String){
            rangeList = range.trim().split(",")
            logger.info("RangeModule's rangeList size is ${rangeList.size}")
        }
        logger.info("RangeModule started.")
        return true
    }

    override fun cleanup() {
    }

}