package com.stuko.stream.api.module.impl

import com.google.gson.Gson
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.common.VarUtils
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import java.io.File
import java.lang.StringBuilder

class FileModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                 logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    var fileName : String? = ""
        get() = field
        set(value) {
            field = value
        }
    var line : String? = ""
        get() = field
        set(value) {
            field = value
        }

    override fun start():Boolean{
        this.fileName = this.getProperty("fileName") ?: StringUtils.genKey("filename_")
        this.line = this.getProperty("line") ?: "---"
        return true
    }
    override fun afterPrepare(collector: OutputCollector?): Unit{
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        try {
            var sb = StringBuilder()
            sb.append(Gson().toJson(map))
            sb.append("\n").append(this.line).append("\n")
            logger.info("FileModule is executed....{} : {}", this.fileName, sb.toString())
            if(this.fileName != null) {
                if(this.fileName!!.indexOf(StringUtils.START_SHARP_TAG) >= 0){
                    logger.info("FileModule You have to replace fileName {} ", this.fileName)
                    this.fileName = VarUtils.replaceVar(this.fileName!!, map)
                    logger.info("FileModule fileName was replaced to {} ", this.fileName)
                }
                while (!writeFile(this.fileName!!, sb.toString())) logger.info("File Write Error : {}", this.fileName)
            }
            this.log(null,map)
        }catch(e:Exception){
            logger.error(e.toString(),e)
            logger.info("FileModule Exception : {} , {}", this.fileName, e.toString())
        }
        return map
    }

    private fun writeFile(path:String, text:String) :Boolean{
        return try {
            if (!File(path).exists()){
                var idx = path!!.lastIndexOf(File.separator)
                var pre = path!!.substring(0,idx)
                var post = path!!.substring(idx+1)
                File(pre).mkdirs()
                try{
                    File(path).createNewFile()
                    File(path).writeText(text)
                }catch(ex:Exception){
                    logger.error(ex.toString(),ex)
                    File(path).writeText(text)
                }
            }else File(path).appendText(text)
            true
        }catch(e:Exception){
            logger.error(e.toString(),e)
            false
        }
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }

}