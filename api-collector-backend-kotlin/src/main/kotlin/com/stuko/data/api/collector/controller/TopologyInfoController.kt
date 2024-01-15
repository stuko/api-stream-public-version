package com.stuko.data.api.collector.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.data.api.collector.common.Log
import com.stuko.data.api.collector.service.TopologyService
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/apicert/")
class TopologyInfoController {

    companion object : Log
    @Value("\${cmd.dir}")
    var dir:String = ""
    @Value("\${cmd.stream.start}")
    var shell:String = ""
    @Value("\${api.collector.kafka.topic}")
    var topicName:String = ""


    val gson = Gson()
    @Autowired
    lateinit var service : TopologyService

    @Autowired
    @Qualifier("kafkaProducer")
    lateinit var producer: KafkaProducer<Any, Any>

    @RequestMapping(path = ["/getTopologyList"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTopologyList(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        val strTopologyGroup: String = map?.get("topologyGroup") as String
        return getResponseListJson(service.list(strTopologyGroup))
    }

    @RequestMapping(path = ["/getTopologyDetail"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTopologyDetail(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        if(map != null) {
            var id:String = map.get("topologyId") as String
            if(service.read(map) != null && service.read(map).size > 0)
                return getResponseJson(service.read(map).get(0));
            else
                return "";
        }else{
            return ""
        }
    }

    @RequestMapping(path = ["/saveTopology"], method = [RequestMethod.POST,RequestMethod.GET], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun saveTopology(httpEntity: HttpEntity<String>): String {
        val map: MutableMap<String, Any>? = getParameter(httpEntity)
        if(map != null) {
            if(map.get("topologyId") == null || "".equals(map.get("topologyId")as String)) {
                val rounded = String.format("%.0f", Math.random() * 10000000)
                var tid: String = "TP_" + System.nanoTime()
                // var tid: String = "TP-" + rounded
                logger.debug("####### tid : {}", tid)
                map.put("topologyId",tid)
                this.service.create(map)
            }else{
                this.service.update(map)
            }

        }
        return ""
    }

    @RequestMapping(path = ["/updateTopology"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateTopology(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        if(map != null) {
            // var id:String = map.get("topologyId") as String
            this.service.update(map)
        }
        return ""
    }

    @RequestMapping(path = ["/deleteTopology"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun deleteTopology(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        if(map != null) {
            // var id:String = map.get("topologyId") as String
            this.service.delete(map)
        }
        return ""
    }

    @RequestMapping(path = ["/readTopology"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun readTopology(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        if(map != null) {
            // var id:String = map.get("topologyId") as String
            return this.getResponseJson(this.service.update(map))
        }
        return ""
    }

    @RequestMapping(path = ["/restartStream"], method = [RequestMethod.POST,RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun restartStream(httpEntity: HttpEntity<String>): String {
        val map: Map<String, Any>? = getParameter(httpEntity)
        this.producer.send(ProducerRecord(topicName, "refresh"))
        return "";
    }

    private fun getParameter(httpEntity: HttpEntity<String>?): MutableMap<String, Any>? {
        val json: String? = httpEntity?.getBody();
        println("-----------------------------")
        println(json)
        println("-----------------------------")
        if(json == null) return null
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val map: MutableMap<String, Any> = gson.fromJson(json, mapType)
        return map
    }

    /*
    DAO 결과를 JSON으로 리턴해주기 위해서는
    해당 결과를 구분이 가능한 이름으로 Wrapping 해줘야 함
    Wrapping 구조는 Map 구조로 해줘야 함.

    example)
       Map<String,String> : read 결과
         --> Map<"read",Map<String,String>>
       List<Map<String,String>> : list 결과
         --> Map<"list",List<Map<String,String>>>
     */
    fun getResponseJson(map: Map<String,Any>?) : String{
        if(map == null){
            return gson.toJson(mapOf("-1" to "CODE"))
        }else{
            return gson.toJson(map)
        }
    }

    fun getResponseListJson(list: List<Map<String,Any>>?) : String{
        if(list == null){
            return gson.toJson(listOf(mapOf("-1" to "CODE")))
        }else{
            return gson.toJson(list)
        }
    }

    fun executeRefresh(map:Map<String, Any>?):Unit{

        val isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows")
        // val builder = ProcessBuilder()
        logger.info("###### execute command ######")
        logger.info("shell : $shell")
        logger.info("dir : $dir")
        try{
            if(map?.get("cmd") != null){
                shell = map?.get("cmd").toString()
            }

            if(map?.get("dir") != null){
                shell = map?.get("dir").toString()
            }

            if (isWindows) {
                logger.info("os is windows")
                Runtime.getRuntime().exec(arrayOf("cmd.exe","/c", "cd $dir && $shell"))
            }else{
                logger.info("os is linux exec : cd $dir && $shell")
                Runtime.getRuntime().exec(arrayOf("bash","-c", "cd $dir && $shell"))
            }
            logger.info("execute $shell is OK")
        }catch(e:Exception){
            logger.error(e.toString(),e)
        }
        logger.info("###### execute command ######")
        // if (isWindows) {
        // if(map == null || map?.get("cmd") == null) builder.command("cmd.exe", "/c", cmd)
        // else  builder.command("cmd.exe", "/c", map?.get("cmd") as String)
        // } else {
        // if(map != null || map?.get("cmd") == null || map?.get("dir") == null ) builder.command("/bin/sh", "-c", "cd " + dir + " && " + cmd)
        // else builder.command("/bin/sh", "-c", "cd " + map?.get("dir") as String + " && " + map?.get("cmd") as String)
        // }
        //builder.directory(File(dir))
        //val process = builder.start()
        //var result : List<Any> = BufferedReader(InputStreamReader(process.inputStream))
        //        .lines()
        //        .collect(Collectors.toList<Any>())
        //logger.debug("####### Execute {} #########" , cmd)
        //logger.debug("####### result {} #########" , result)
    }
}