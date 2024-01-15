package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.config.Singleton
import com.stuko.stream.api.module.IModule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values

class DBMSModule (moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                  logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    companion object : Log
    var instanceKey: String? = null

    override fun start():Boolean{
        try {
            var config = HikariConfig()
            if(this.getProperty("url") != null) config.jdbcUrl = this.getProperty("url")
            if(this.getProperty("driver") != null) config.driverClassName = this.getProperty("driver")
            if(this.getProperty("id") != null) config.username = this.getProperty("id")
            if(this.getProperty("pw") != null) config.password = this.getProperty("pw")
            if(this.getProperty("max") != null) config.maximumPoolSize = this.getProperty("max").toInt()
            if(this.getProperty("min") != null) config.minimumIdle = this.getProperty("min").toInt()
            if(this.getProperty("validation") != null) config.connectionTestQuery = this.getProperty("validation")
            if(this.getProperty("init") != null) config.connectionInitSql = this.getProperty("init")
            var dataSource = HikariDataSource(config)
            Singleton.putDataSource(this.getProperty("name"), dataSource)
        }catch(e:Exception){
            logger.error("--- Data Source( ${this.getProperty("name")} ) is not ready ---")
            logger.error(e.toString())
            logger.error("--- Data Source( ${this.getProperty("name")} ) is not ready ---")
            return false
        }
        return true
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }

    override fun afterPrepare(collector: OutputCollector?): Unit{
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.info("----------- DBMSModule ------------")
        logger.info("######## DBMSModule's parameter is {}", map)
        logger.info("----------- DBMSModule ------------")
        if (collector != null) {
            this.log(map,null )
            collector.emit(Values(map))
        }
        return map
    }

}

