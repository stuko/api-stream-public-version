package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import javax.script.*

class FunctionModule (moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                      logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    var functionName:String = ""
    var source:String = ""

    @Transient
    var compiledScript: CompiledScript? = null
        get() = field
        set(value) {
            field = value
        }

    @Transient
    var engine: ScriptEngine? = null
        get() = field
        set(value) {
            field = value
        }

    public override fun execute(param: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.info("----------- FunctionModule ------------")
        logger.info("FunctionModule is called ")
        logger.info("----------- FunctionModule ------------")
        this.log(param,null)
        if(this.engine == null){
            param!!["Error"] = "Engine is NULL"
            return param
        }
        var bind: Bindings? = this.engine?.createBindings()
        for (n: String in param!!.keys) {
            try {
                if(param?.get(n) is String) {
                    bind?.put(n, param.get(n) as String)
                }else bind?.put(n, param?.get(n))
            }catch(e :Exception){
                logger.error(e.toString(), e)
                logger.info("-------- Function Module Bind Exception -----------")
                logger.info("Function : {}" , this.source)
                logger.info("Parameter : {}" , param)
                logger.info("Error : {}" , e.toString())
                logger.info("-------- Function Module Bind Exception -----------")
            }
        }
        bind?.put("parameter", param)

        if (this.compiledScript != null) {
            try {
                var objResult = this.compiledScript!!.eval(bind) ?: ""

                if(objResult is String){
                    param[this.functionName] = objResult as String
                }else if(objResult is Double){
                    param[this.functionName] = objResult as Double
                }else if(objResult is Int){
                    param[this.functionName] = objResult as Int
                }else if(objResult is Long){
                    param[this.functionName] = objResult as Long
                }else if(objResult is Map<*, *>) {
                    param.putAll(objResult as Map<String, Any>);
                }else{
                    param[this.functionName] = objResult
                }
                logger.info("-------- Function Module Result -----------")
                logger.info("Script : {}" , this.source)
                logger.info("Parameter : {}" , param)
                logger.info("Result : {}" , param[this.functionName])
                logger.info("-------- Function Module Result -----------")
            } catch (e: Exception) {
                logger.info("-------- Function Module Error Result -----------")
                logger.info("Script : {}" , this.source)
                logger.info("Parameter : {}" , param)
                logger.error(e.toString(),e)
                logger.info("-------- Function Module Error Result -----------")
            }
        } else {
            logger.info("######### compiled script is null")
        }
        this.log(null, param)
        return param
    }

    override fun afterPrepare(collector: OutputCollector?) {
        if(this.getProperty("name") == null
            || this.getProperty("source") == null
            || this.getProperty("name").isEmpty()
            || this.getProperty("source").isEmpty()) return
        this.functionName = this.getProperty("name").trim()
        logger.info("Function Module's name is $this.functionName")

        this.source = this.getProperty("source").trim()
        logger.info("Function Module's source is $this.source")
        try {
            this.engine = ScriptEngineManager().getEngineByName("javascript")!!
            logger.info("Function Module create the script engine")
            logger.info("######### create javascript engine")
            this.compiledScript = (engine as Compilable).compile(this.source)
            logger.info("######### script is compiled")
        }catch(e:Exception){
            logger.info("Function Module Engine Error : ${e.toString()}")
            logger.error(e.toString(),e)
        }
    }

    override fun start() :Boolean{
        return true
    }

    override fun cleanup() {
    }

}
