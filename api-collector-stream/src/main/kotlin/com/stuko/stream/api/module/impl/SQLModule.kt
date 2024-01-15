package com.stuko.stream.api.module.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.config.Singleton
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData


class SQLModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
               logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    companion object : Log
    private var instanceKey: String? = null
    private var reuseConnection: Boolean = true
    private val MAX_EMIT_COUNT = 50
    private var pause:Boolean = false
    override fun start():Boolean{
        return connect()
    }

    private fun getConnection(ds:String, key:String):Connection? {
        var con: Connection? = null
        try{
            if(!reuseConnection) con = Singleton.getDataSource(ds)?.connection
            else {
                try {
                    con = Singleton.getConnection(key)
                    if (con == null || con.isClosed) {
                        logger.info("datasource is {} ,  {} ", ds, key)
                        con = getNewConnection(ds, key)
                    }
                } catch (e: Exception) {
                    logger.error("--- DataSource() is not ready ---")
                    logger.error(e.toString(), e)
                    logger.error("--- DataSource() is not ready ---")
                }
            }
        }catch(ex:Exception){
            logger.error(ex.toString(),ex)
            con = this.getNewConnection(ds, key)
        }
        return con
    }

    private fun getNewConnection(ds: String , key: String): Connection? {
        return if (Singleton.getDataSource(ds) != null) {
            val con = Singleton.getDataSource(ds)?.connection
            if (con != null) {
                Singleton.putConnection(key, con)
            }
            logger.info("found datasource of {}", ds)
            con
        } else {
            logger.error("DataSource Exception.............")
            null
        }
    }

    private fun connect():Boolean {
        this.instanceKey = StringUtils.genKey("connection")
        this.getConnection(this.getProperty("datasource"), this.instanceKey!!)
        return true
    }

    override fun afterPrepare(collector: OutputCollector?) {
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {

        logger.info("----------- SQLModule ------------")
        logger.info("######## SQLModule's parameter is {}", map)
        logger.info("----------- SQLModule ------------")

        if (Singleton.getDataSource(this.getProperty("datasource")) == null) {
            map?.put("ERROR", "Connection is NULL")
            this.log(null, map)
            return map
        }
        val ds = this.getProperty("datasource")
        var pstmt: PreparedStatement? = null
        var resultSet: ResultSet? = null
        var con: Connection? = null
        try {
            run {
                var sql: String = this.getProperty("sql")
                logger.error(" Not error : [SQLModule] sql is $sql")
                val list: MutableList<String?> = mutableListOf()
                while (sql.indexOf(StringUtils.START_SHARP_TAG) >= 0) {
                    sql = StringUtils.getReplacedMyBatisSql(sql, StringUtils.START_SHARP_TAG, list).toString()
                }
                logger.info("converted sql is {}", sql)
                try {
                    sql = StringUtils.replacedAllMyBatisDollarSign(sql, map) ?: sql
                } catch (ex: Exception) {
                    logger.error(ex.toString(), ex)
                    logger.warn("Error occurred , but continue.....")
                }
                try {
                    con = this.getConnection(ds,this.instanceKey!!)
                    pstmt = con?.prepareStatement(sql)
                    // pstmt = this.instanceKey?.let { Singleton.getConnection(it)?.prepareStatement(sql) }
                    // pstmt = Singleton.getDataSource(this.getProperty("datasource"))!!.connection.prepareStatement(sql)
                } catch (ex: Exception) {
                    logger.error(ex.toString() + "\n" + map.toString() + "\n" + sql)
                    var reconnected = false
                    while (!reconnected) {
                        try {
                            logger.info("SQLModule .... Connection Retry....")
                            con = this.getConnection(ds,this.instanceKey!!)
                            pstmt = con?.prepareStatement(sql)
                            // this.connect()
                            // pstmt = this.instanceKey?.let { Singleton.getConnection(it)?.prepareStatement(sql) }
                            // pstmt = Singleton.getDataSource(this.getProperty("datasource"))!!.connection.prepareStatement(sql)
                            reconnected = true
                        } catch (re_ex: Exception) {
                            logger.info("SQLModule .... Connection Exception ... Retry....")
                            try {
                                Thread.sleep(5000)
                            } catch (th_ex: Exception) {
                                logger.error(th_ex.toString())
                            }
                        }
                    }
                }
                if (pstmt != null) {
                    logger.info("ok sql is prepared : {}", sql)
                    var i = 1
                    list.forEach { n ->
                        val obj: Any? = map?.get(n)
                        if (obj != null) {
                            if (map[n] is Double) pstmt!!.setDouble(i++, map[n] as Double)
                            else pstmt!!.setString(i++, map[n] as String)
                        } else logger.error("SQLModule can not bind parameter of {}", n)
                    }
                    if (sql.trim().toLowerCase().startsWith("select")) {
                        logger.error(" Not error : ok sql is select : {}", sql)
                        // pstmt!!.fetchSize = 100
                        resultSet = pstmt!!.executeQuery()
                        val meta: ResultSetMetaData = resultSet!!.metaData
                        var i = 1
                        val cols: MutableList<String> = mutableListOf()
                        while (i <= meta.columnCount) {
                            cols.add(meta.getColumnLabel(i))
                            i++
                        }

                        var curEmitCount = 0
                        var list: MutableList<Any>? = mutableListOf()

                        while (resultSet!!.next()) {
                            val p: MutableMap<String, Any> = mutableMapOf()
                            cols.forEach { n ->
                                p[n] = resultSet!!.getString(n) ?: ""
                            }
                            logger.info("result record is {}", p)
                            if (collector != null) {
                                this.log(map, null)
                                // map?.putAll(p)
                                map?.forEach { (k, v) ->
                                    if (!p.containsKey(k)) p[k] = v
                                }
                                // p?.putAll(map)

                                if (curEmitCount >= MAX_EMIT_COUNT) {
                                    collector.emit(Values(list))
                                    list = mutableListOf()
                                    curEmitCount = 0
                                } else {
                                    list?.add(p)
                                    curEmitCount++
                                }
                                this.log(null, p)
                            }
                        }

                        if (curEmitCount != 0) {
                            collector?.emit(Values(list))
                        }

                    } else {
                        logger.info(" Not error : ok sql is dml : {}", sql)
                        this.log(map, null)
                        pstmt!!.executeUpdate()
                        collector?.emit(Values(map))
                        this.log(null, map)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("SQL Error :  {}", e.toString())
            val ste: Array<StackTraceElement> = e.stackTrace
            val className = ste[0].className
            val methodName = ste[0].methodName
            val lineNumber = ste[0].lineNumber
            val fileName = ste[0].fileName
            logger.error("SQL Error : Parameter {}", map)
            logger.error("SQL Error : SQL {}", this.getProperty("sql"))
            logger.error(e.toString(), e)
            logger.error("######### Reload DataSource start... ###########")
            try{
                if(!pause) {
                    pause = true
                    Singleton.reloadConnection(this.getProperty("datasource"))
                    this.connect()
                }
            }catch(ee:Exception){}
            logger.error("######### Reload DataSource complete ###########")
            if (collector != null) {
                map?.put(
                    "ERROR",
                    "$e :  class = $className, methodName = $methodName , lineNumber = $lineNumber , fileName = $fileName"
                )
                collector.emit(Values(map))
                this.log(null, map)
            }
        } finally {
            try { if(resultSet != null )resultSet?.close()} catch (e1: Exception) {}
            try { if(pstmt != null )pstmt?.close()} catch (e1: Exception) {}
            if (!reuseConnection) {
                try { if(con != null )con?.close()} catch (e1: Exception) {}
            }
            this.async = true
        }
        return map
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }
}


