package com.stuko.stream.api.common

import com.stuko.stream.api.config.StreamConfig.Companion.logger
import com.stuko.stream.api.module.impl.SelectorModule

class MapUtils {

    companion object{

        val tagStart:String = "{="
        val tagEnd:String = "}"
        val stringStart = "\""
        val stringEnd = "\""

        public fun replaceFromMap(pfx:String?, src:String, map:Map<String,Any>) : String{
            var source = src
            var prefix = pfx
            if(prefix != null && prefix != "") prefix = "$prefix."
            map.forEach{ k,v ->
                var curPrefix = "$prefix$k"
                println("Key is $curPrefix")
                source = replaceByKind(curPrefix, source, v)
            }
            return source
        }

        public fun replaceByKind(prefix:String?, src:String, v:Any?):String {
            var source = src
            if(v == null) return source

            when (v) {
                is MutableMap<*, *> -> {
                    source = replaceFromMap(prefix, source, v as Map<String, Any>)
                }
                is MutableList<*> -> {
                    (v as MutableList<*>).forEachIndexed { index, it ->
                        if (it is Map<*, *>) {
                            source = replaceFromMap("$prefix[$index]", source, it as Map<String, Any>)
                        } else source = replaceByKind("$prefix[$index]", source, it)
                    }
                }
                is String -> {
                   source = source.replace(tagStart+prefix.toString()+tagEnd,stringStart + (v as String) + stringEnd)
                }
                is Integer -> {
                    source = source.replace(tagStart+prefix.toString()+tagEnd,(v as String))
                }
                is Long -> {
                    source = source.replace(tagStart+prefix.toString()+tagEnd,(v as String))
                }
            }
            return source
        }

        public fun getJSONData(key:String, map:MutableMap<String,Any>?) : String{
            var strResult:String = "";
            var prefix = if(key.trim().indexOf(".") >= 0)key.trim().substring(0, key.trim().indexOf(".")) else key.trim()
            logger.info("prefix is {}", prefix)
            var postfix = if(key.trim().indexOf(".") >= 0)key.trim().substring(key.trim().indexOf(".") + 1) else key.trim()
            logger.info("postfix is {}", postfix)
            var obj: Any? = map?.get(prefix)
            if (obj != null) {
                if (obj is Map<*, *>) {
                    logger.info("selector's value type is Map")
                    var m: Map<String, Any> = obj as Map<String, Any>
                    strResult = getJSONData(postfix, m as MutableMap<String, Any>?)
                } else if (obj is MutableMap<*, *>) {
                    logger.info("selector's value type is MutableMap")
                    var m: MutableMap<String, Any> = obj as MutableMap<String, Any>
                    strResult = getJSONData(postfix,m)
                } else if (obj is List<*>) {
                    logger.info("selector's value type is List")
                    var l: List<Any> = obj as List<Any>
                } else if (obj is MutableList<*>) {
                    logger.info("selector's value type is MutableList")
                    var l: MutableList<Any> = obj as MutableList<Any>
                    strResult = l.joinToString(",")
                } else if (obj is String) {
                    logger.info("selector's value type is String")
                    strResult = obj as String
                } else if (obj is Int) {
                    logger.info("selector's value type is Int")
                    var intObject = obj as Int
                    strResult = intObject.toString()
                } else if (obj is Long) {
                    logger.info("selector's value type is Long")
                    var longObject = obj as Long
                    strResult = longObject.toString()
                } else if (obj is Boolean) {
                    logger.info("selector's value type is Boolean")
                    var boolObject = obj as Boolean
                    strResult = boolObject.toString()
                } else if (obj is Double) {
                    logger.info("selector's value type is Double")
                    var doubleObject = obj as Double
                    strResult = doubleObject.toString()
                } else {
                    logger.info("selector's value type is not defined Type")
                    strResult = obj.toString()
                }
            }
            return strResult
        }
    }
}

