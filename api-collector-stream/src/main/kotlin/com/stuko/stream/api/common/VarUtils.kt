package com.stuko.stream.api.common

import org.slf4j.LoggerFactory

class VarUtils {
    companion object {
        val logger = LoggerFactory.getLogger(VarUtils::class.java)
        private const val YYYYMMDD:String = "YYYYMMDD"
        private const val YYYYMMDDHH:String = "YYYYMMDDHH"
        private const val YYYYMMDDHHMM:String = "YYYYMMDDHHMM"
        private const val YYYYMMDDHHMMSS:String = "YYYYMMDDHHMMSS"

        public fun replaceVar(source:String, map: MutableMap<String, Any>?): String{
            logger.info("FileModule call replaceVar : {}" , source)
            var tmpMap:MutableMap<String,Any> = mutableMapOf()
            tmpMap[VarUtils.YYYYMMDD] = StringUtils.getTime("yyyyMMdd")
            tmpMap[VarUtils.YYYYMMDDHH] = StringUtils.getTime("yyyyMMddHH")
            tmpMap[VarUtils.YYYYMMDDHHMM] = StringUtils.getTime("yyyyMMddHHmm")
            tmpMap[VarUtils.YYYYMMDDHHMMSS] = StringUtils.getTime("yyyyMMddHHmmss")
            logger.info("FileModule tmpMap is ready : {}" , source)
            if (map != null) {
                tmpMap.putAll(map)
                logger.info("FileModule tmpMap map : {}" , tmpMap)
                logger.info("FileModule tmpMap putAll map : {}" , source)
            }
            var src = StringUtils.replacedAllMyBatisSharpSign(source, tmpMap)
            logger.info("FileModule replace to : {}" , src)
            return src ?: ""
        }
    }

}
