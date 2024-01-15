package com.stuko.stream.api.common

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

class StringUtils{

    companion object {
        val logger = LoggerFactory.getLogger(StringUtils::class.java)
        public val START_DOLLOR_TAG = "{="
        public val START_SHARP_TAG = "#{"
        public val END_TAG = "}"
        public var REPL_MARK = " ? "

        fun genKey (prefix:String) :String{
            return prefix + (Math.random()*1000000) + System.currentTimeMillis() + System.nanoTime()
        }

        fun getTime () :String{
            var sdf : SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS")
            var now = sdf.format(System.currentTimeMillis())
            return now
        }
        fun getTime (format:String) :String{
            var sdf : SimpleDateFormat = SimpleDateFormat(format)
            var now = sdf.format(System.currentTimeMillis())
            return now
        }

        fun replacedSign(sql: String, tag:String, param: MutableMap<String, Any>?): String {
            if (sql.indexOf(tag) < 0) return sql
            val newSql = StringBuilder()
            val endTag = END_TAG
            val s = sql.indexOf(tag)
            val e = sql.indexOf(endTag,s)
            val name = sql.substring(s + tag.length, sql.indexOf(endTag, s))
            logger.info("param is : {}", param)
            logger.info("replace name is : {}", name)
            if (param != null) {
                if (param.containsKey(name)) {
                    logger.info("param contains : [{}]", name)
                    newSql.append(sql.substring(0, s)).append(param[name].toString()).append(sql.substring(e + endTag.length))
                }else{
                    logger.info("param does not contain : [{}]", name)
                    val value:String = MapUtils.getJSONData(name,param)
                    if(value != null && value.trim().isNotEmpty()){
                        newSql.append(sql.substring(0, s)).append(value).append(sql.substring(e + endTag.length))
                    }else{
                        newSql.append(sql)
                    }
                }
            }
            return newSql.toString()
        }

        fun replacedMyBatisDollarSign(sql: String, param: MutableMap<String, Any>?): String? {
            return replacedSign(sql, START_DOLLOR_TAG, param)
        }
        fun replacedMyBatisSharpSign(sql: String, param: MutableMap<String, Any>?): String? {
            return replacedSign(sql, START_SHARP_TAG, param)
        }

        fun replacedAllMyBatisDollarSign(sql: String, param: MutableMap<String, Any>?): String? {
            var tmp = sql;
            while(tmp.indexOf(StringUtils.START_DOLLOR_TAG) >= 0){
                tmp = replacedSign(tmp,START_DOLLOR_TAG,param)
            }
            return tmp
        }

        fun replacedAllMyBatisSharpSign(sql: String, param: MutableMap<String, Any>?): String? {
            var tmp = sql;
            while(tmp.indexOf(StringUtils.START_SHARP_TAG) >= 0){
                tmp = replacedSign(tmp,START_SHARP_TAG,param)
                logger.info("tmp : {}" , tmp)
            }
            return tmp
        }

        fun getReplacedMyBatisSql(sql: String, tag:String, list: MutableList<String?>): String? {
            if (sql.indexOf(tag) < 0) return sql
            val newSql = java.lang.StringBuilder()
            val endTag = END_TAG
            val s = sql.indexOf(tag)
            val e = sql.indexOf(endTag, s)
            val name = sql.substring(s + tag.length, sql.indexOf(endTag, s))
            newSql.append(sql.substring(0, s)).append(REPL_MARK).append(sql.substring(e + endTag.length))
            list.add(name.trim { it <= ' ' })
            return newSql.toString()
        }
    }

}