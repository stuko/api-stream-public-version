package com.stuko.data.api.collector.common

import java.text.SimpleDateFormat

class StringUtils{

    companion object {

        public val START_DOLLOR_TAG = "\${"
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

        fun replacedSign(sql: String, startTag:String, param: MutableMap<String, Any>?): String {
            if (sql.indexOf(START_DOLLOR_TAG) < 0) return sql
            val newSql = StringBuilder()
            val startTag = START_DOLLOR_TAG
            val endTag = END_TAG
            val s = sql.indexOf(startTag)
            val e = sql.indexOf(endTag)
            val name = sql.substring(s + startTag.length, sql.indexOf(endTag, s))
            if (param != null) {
                if (param.containsKey(name)) {
                    newSql.append(sql.substring(0, s)).append(param[name].toString()).append(sql.substring(e + endTag.length))
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
            while(tmp.indexOf(StringUtils.START_DOLLOR_TAG) >= 0) tmp = replacedSign(tmp,START_DOLLOR_TAG,param)
            return tmp
        }

        fun replacedAllMyBatisSharpSign(sql: String, param: MutableMap<String, Any>?): String? {
            var tmp = sql;
            while(tmp.indexOf(StringUtils.START_SHARP_TAG) >= 0) tmp = replacedSign(tmp,START_SHARP_TAG,param)
            return tmp
        }

        fun getReplacedMyBatisSql(sql: String, tag:String, list: MutableList<String?>): String? {
            if (sql.indexOf(tag) < 0) return sql
            val newSql = java.lang.StringBuilder()
            val startTag = tag
            val endTag = END_TAG
            val s = sql.indexOf(startTag)
            val e = sql.indexOf(endTag)
            val name = sql.substring(s + startTag.length, sql.indexOf(endTag, s))
            newSql.append(sql.substring(0, s)).append(REPL_MARK).append(sql.substring(e + endTag.length))
            list.add(name.trim { it <= ' ' })
            return newSql.toString()
        }
    }

}