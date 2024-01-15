package com.stuko.stream.api.module.impl

import com.google.gson.Gson
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.config.Singleton
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values

class MongoModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int,
                logBufferManager: AbstractLogBufferManager ) : AbstractBoltModule(moduleType, moduleKey, moduleCount, logBufferManager) {

    companion object : Log
    var mongoDB:String? = null
    var mongoCollection:String? = null
    var mongoSql:String? = null
    var mongoSqlType:String? = null

    override fun start() :Boolean{
        this.mongoDB = this.getProperty("db")
        this.mongoCollection = this.getProperty("collection")
        this.mongoSql = this.getProperty("mongoSql")
        this.mongoSqlType = this.getProperty("mongoSqlType")
        return true
    }

    override fun afterPrepare(collector: OutputCollector?): Unit {
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        var dao = Singleton.getMongoDAO("MongoDAO")!!
        if(this.mongoDB == null) {
            logger.warn("this.mongoDB is NULL")
            return null
        }
        if(this.mongoCollection == null) {
            logger.warn("this.mongoCollection is NULL")
            return null
        }
        if(this.mongoSql == null) {
            logger.warn("this.mongoSql is NULL")
            return null
        }
        if(this.mongoSqlType == null) {
            logger.warn("this.mongoSqlType is NULL")
            return null
        }
        var sql: String = this.mongoSql!!
        var list: MutableList<String?> = mutableListOf()
        while (sql.indexOf(StringUtils.START_SHARP_TAG) >= 0) {
            sql = StringUtils.getReplacedMyBatisSql(sql, StringUtils.START_SHARP_TAG, list).toString()
        }
        logger.info("converted sql is {}", sql)
        try{
            sql = StringUtils.replacedAllMyBatisDollarSign(sql,map) ?: sql
        }catch(ex:Exception){
            logger.error(ex.toString(),ex)
            logger.warn("Error occured , but continue.....")
        }

        var mongoQL = Gson().fromJson<MutableMap<String,Any>>(sql,MutableMap::class.java)

        if(this.mongoSqlType?.trim() == "SELECT") {
            var result: MutableList<MutableMap<String, Any>> = dao.find(this.mongoDB!!, this.mongoCollection!!,mongoQL!!)
            if(result.size != 0) {
                val MAX_EMIT_COUNT = 50
                var curEmitCount = 0
                var list:MutableList<Any>? = mutableListOf()
                // 결과가 있으면
                result.forEach { m ->
                    // 스트림 파라미터
                    var p:MutableMap<String, Any> = mutableMapOf()
                    // 스트림 파라미터에 파라미터를 넣는다
                    p.putAll(map!!)
                    // 스트림 파라미터에 결과를 넣는다
                    p.putAll(m)

                    if(p != null) {
                        // 스트림으로 보낼양을 최대 갯수 만큼만 보낸다
                        if (curEmitCount >= MAX_EMIT_COUNT) {
                            collector?.emit(Values(list))
                            list = mutableListOf()
                            curEmitCount = 0
                        } else {
                            list?.add(p)
                            curEmitCount++
                        }
                    }
                    this.log(null, p)
                }
            }
        }else if(this.mongoSqlType?.trim() == "INSERT"
              || this.mongoSqlType?.trim() == "UPDATE"
              || this.mongoSqlType?.trim() == "DELETE"
        ) {
            logger.info("ok mongoSql is dml : ${this.mongoSql}")
            this.log(map, null)
            when (this.mongoSqlType) {
                "INSERT" -> dao.insert(this.mongoDB!!,this.mongoCollection!!,mongoQL!!)
                // map!! : search condition and update data
                // "UPDATE" -> this.dao.update(this.mongoDB!!,this.mongoCollection!!,mongoQL!!,mongoQL!!)
                "DELETE" -> dao.delete(this.mongoDB!!,this.mongoCollection!!,mongoQL!!)
            }
            collector?.emit(Values(map))
            this.log(null, map)
        }else{
            logger.warn("this.mongoSqlType is NOT SELECT,INSERT,UPDATE,DELETE : ${this.mongoSqlType}")
            return null
        }
        this.async = true
        return map
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
    }

}
