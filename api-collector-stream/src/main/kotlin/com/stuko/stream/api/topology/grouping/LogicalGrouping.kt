package com.stuko.stream.api.topology.grouping

import com.stuko.stream.api.common.Log
import org.apache.storm.generated.GlobalStreamId
import org.apache.storm.grouping.CustomStreamGrouping
import org.apache.storm.task.WorkerTopologyContext
import java.io.Serializable
import java.util.*
import javax.script.*

class LogicalGrouping : CustomStreamGrouping , Serializable{

    companion object : Log

    private var random: Random? = null
    private var targetTasks: List<Int>? = null
    private var numTasks = 0

    var script:String = ""
        get() = field
        set(value) {
            field = value
        }

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

    constructor(script : String) {
        logger.info("######### new LogicalGrouping")
        this.script = script
        random = Random()
        logger.info("######### script is {}", this.script)
    }

    override fun prepare(context: WorkerTopologyContext?, stream: GlobalStreamId?, targetTasks: MutableList<Int>?) {
        this.engine = ScriptEngineManager().getEngineByName("javascript")!!
        logger.info("######### create javascript engine")
        this.compiledScript = (engine as Compilable).compile(this.script)
        logger.info("######### script is compiled")
        this.targetTasks = targetTasks;
        if (targetTasks != null) {
            this.numTasks = targetTasks.size
        };
    }

    private fun getMapValues(values: Any) : MutableMap<String,Any>{
        var map = mutableMapOf<String,Any>()
        if(values is List<*>){
            if(values[0] is MutableMap<*, *>){
                return values[0] as MutableMap<String, Any>
            }else if(values.get(0) is List<*>){
                return getMapValues(values[0] as MutableList<Any>)
            }else{
                map["result"] = values
            }
        }else if(values is MutableMap<*, *>){
            return values as MutableMap<String, Any>
        }else{
            map["result"] = values
        }
        return map
    }

    override fun chooseTasks(taskId: Int, values: MutableList<Any>?): MutableList<Int> {
        var result: Boolean = false
        if (values != null) {
            try {
                logger.info("#####  LogicalGroup : Value is NOT NULL , result is {} " , result)
                logger.info("##### LogicalGroup : Value is NOT NULL , value is {} " , values)

                var map: MutableMap<String, Any>? = getMapValues(values[0])
                if (map != null) {
                    var bind: Bindings? = this.engine?.createBindings()
                    for (n: String in map.keys) {
                        try {
                            if((n != null) && n.trim().isNotEmpty()) {
                                logger.info("LogicalGrouping Parameter key is {} , value is {}" , n, map[n])
                                if (map[n] is String) {
                                    bind?.put(n, (map[n] ?: "") as String)
                                } else bind?.put(n, map[n] ?: "")
                            }
                        }catch(e :Exception){
                            logger.error(e.toString(), e)
                            logger.info("-------- Logical Grouping Bind Exception -----------")
                            logger.info("Script : {}" , this.script)
                            logger.info("Parameter : {}" , map)
                            logger.info("Error : {}" , e.toString())
                            logger.info("-------- Logical Grouping Bind Exception -----------")
                        }
                    }
                    logger.info("### Logical Grouping middle result : {} " , result)

                    if (this.compiledScript != null) {
                        try {
                            result = this.compiledScript!!.eval(bind) as Boolean

                            logger.info("-------- Logical Grouping Result -----------")
                            logger.info("Script : {}" , this.script)
                            logger.info("Parameter : {}" , map)
                            logger.info("Result : {}" , result)
                            logger.info("-------- Logical Grouping Result -----------")

                        } catch (e: Exception) {
                            logger.info("-------- Logical Grouping Error Result -----------")
                            logger.info("Script : {}" , this.script)
                            logger.info("Parameter : {}" , map)
                            logger.error(e.toString(),e)
                            logger.info("-------- Logical Grouping Error Result -----------")

                            result = false;
                        }
                    } else {
                        logger.info("######### compiled script is null")
                    }
                }else{
                    logger.info("### LogicalGroup : Map is NULL , result is {} " , result)
                }
            }catch(e:Exception){
                logger.error(e.toString(),e)
                logger.info("-------- Logical Grouping Choose Task Exception -----------")
                logger.info("Script : {}" , this.script)
                logger.info("Error : {}" , e.toString())
                logger.info("-------- Logical Grouping Choose Task Exception -----------")

            }
        }else{
            logger.info("### LogicalGroup : Value is NULL , result is {} " , result)
        }
        if(result) {
            val index = random!!.nextInt(numTasks)
            return Collections.singletonList(targetTasks!![index])
        }else{
            return mutableListOf()
        }
    }
}