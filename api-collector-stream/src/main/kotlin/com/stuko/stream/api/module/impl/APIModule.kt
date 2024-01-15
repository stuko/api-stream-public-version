package com.stuko.stream.api.module.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.common.MapUtils
import com.stuko.stream.api.common.StringUtils
import com.stuko.stream.api.config.Singleton.map
import com.stuko.stream.api.module.IModule
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import kotlinx.serialization.json.JsonNull.int
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.apache.storm.task.OutputCollector
import org.apache.storm.tuple.Values
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class APIModule(moduleType: IModule.KIND, moduleKey: String, moduleCount: Int, val apiUrl:String, val apiFormat:String, val certList:MutableList<MutableMap<String,Any>>, val limitCheckIP:String?, val limitCheckPort:String?, logBufferManager: AbstractLogBufferManager
) : AbstractBoltModule(moduleType, moduleKey, moduleCount , logBufferManager){

    companion object : Log

    var proxyIp:String = ""

    var proxyPort:String = ""
    var method:String = ""
    var paramConstMap:MutableMap<String,String> = mutableMapOf()
    var paramVarMap:MutableMap<String,String> = mutableMapOf()
    var headerConstMap:MutableMap<String,Any> = mutableMapOf()
    var headerVarMap:MutableMap<String,Any> = mutableMapOf()
    var cMap= mutableMapOf<String,Any>()
    var timerTask: Timer? = null

    override fun afterPrepare(collector: OutputCollector?): Unit{
        /*
        timerTask = timer(period=5000){
            with(certList) {
                shuffle()
            };
            for(it : MutableMap<String, Any>? in certList) {
                var period = ((it?.get("period") ?: "60000") as String) ?: "60000"
                var percent = ((it?.get("percent") ?: "100") as String) ?: "100"
                var wait = ((it?.get("wait") ?: "100") as String) ?: "100"
                var max = ((it?.get("limit") ?: "100") as String) ?: "100"

                var builder:FormBody.Builder = FormBody.Builder()
                builder.add("serverIp",brokerServer)
                builder.add("topicName",it?.get("certId") as String)
                builder.add("moduleName",moduleName)
                builder.add("limit",max)
                builder.add("period",period)

                logger.info("Limit Check parameter's server is ${brokerServer}")
                logger.info("Limit Check parameter's topicName is ${it?.get("certId") as String}")
                logger.info("Limit Check parameter's moduleName is ${moduleName}")
                logger.info("Limit Check parameter's limit is ${max}")
                logger.info("Limit Check parameter's period is ${period}")
                var map: MutableMap<String, Any>? = null
                var message = "ERROR"
                try {
                    val formBody: RequestBody = builder.build()
                    val reqBuilder: Request.Builder = Request.Builder()
                    val request: Request =
                        reqBuilder.url("http://${limitCheckIP}:${limitCheckPort}/stream/checkLimit")
                            .post(formBody)
                            .build()
                    val response: Response = client().newCall(request).execute()
                    message = response.body?.string()!!
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    logger.info("Limit Check(http://${limitCheckIP}:${limitCheckPort}/stream/checkLimit) result is ${message}")
                    map = Gson().fromJson(message, mapType)
                    val result = map?.get("result") as String
                    logger.info("Limit Check - result :  ${result}")
                    if("true" == result){
                        cMap = it
                        break
                    }
                }catch(e:Exception){
                    // logger.error("
                    // Limit Check - error :  ${e.toString()}")
                    map = mutableMapOf()
                    map["ERROR"] = e.toString()
                    if (message != null) {
                        map["source"] = message
                    }
                }
            }

        }
        */
    }

    fun start(moduleName:String) : Unit{
    }

    override fun start():Boolean {
        logger.info("### APIModule>start> ")
        brokerServer = this.getProperty("brokerServer")
        numOfPartition = this.getProperty("numOfPartition")
        replicationFactor = this.getProperty("replicationFactor")
        val key: String = this.getProperty("api")
        val parameter: String = this.getProperty("parameter")
        val header: String = this.getProperty("header")
        method = this.getProperty("method")
        proxyIp = this.getProperty("proxyIp")
        proxyPort = this.getProperty("proxyPort")
        logger.info("### APIModule>start> read property ")

        if (parameter != null && !"".equals(parameter)) {
            logger.info("### APIModule>start> parameter is not null {} ", parameter)
            val parameters: List<String> = parameter.split(",")
            parameters.forEach { s ->
                logger.info("### APIModule>start> parameter>foreach> read {} ", s)
                val ss: List<String> = s.split("=")
                val n: String = ss[0]
                val v: String = ss[1]
                if (v.trim().startsWith(StringUtils.START_SHARP_TAG)) {
                    this.paramVarMap[n] = v.replace(StringUtils.START_SHARP_TAG, "").replace(StringUtils.END_TAG, "")
                } else this.paramConstMap[n] = v
            }
        }

        if (header != null && "" != header) {
            logger.info("### APIModule>start> header is not null")
            val headers: List<String> = header.split(",")
            headers.forEach { s ->
                logger.info("### APIModule>start> header>foreach> read {}", s)
                val ss: List<String> = s.split("=")
                val n: String = ss[0]
                val v: String = ss[1]
                if (v.trim().startsWith(StringUtils.START_SHARP_TAG)) {
                    this.headerVarMap[n] = v.replace(StringUtils.START_SHARP_TAG, "").replace(StringUtils.END_TAG, "")
                } else this.headerConstMap[n] = v
            }
        }
        logger.info("### APIModule> timer is starting.......")
        this.start(this.moduleName)
        logger.info("### APIModule> timer started.")
        return true
    }

    override fun execute(map: MutableMap<String, Any>?, collector: OutputCollector?): MutableMap<String, Any>? {
        logger.info("### APIModule>execute> before create OkHttpClient")
        val client = OkHttpClient()
        logger.info("--------- APIModule -----------")
        logger.info("####### APIModule's parameter is {}", map)
        logger.info("####### APIModule's Info is {} , {}", apiUrl, apiFormat)
        
		if(certList != null){
			certList.forEach { c ->
				logger.info("cert info is {} , {}", c["certId"], c["limit"])
			}
		}
        logger.info("--------- APIModule -----------")
        // private val basicHeader:BasicHeader = BasicHeader()

        logger.info("### APIModule>execute> before create LimitManager")

        numOfPartition = numOfPartition?: "1"
        replicationFactor = replicationFactor?: "1"

        try {
            if(cMap != null) {
                var period = ((cMap["period"] ?: "60000") as String) ?: "60000"
                var percent = ((cMap["percent"] ?: "100") as String) ?: "100"
                var wait = ((cMap["wait"] ?: "100") as String) ?: "100"
                var max = ((cMap["limit"] ?: "100") as String) ?: "100"
                if(map != null) map?.putAll(cMap)
                if(map != null) map?.put("Retry", "false")
                this.run(this.apiUrl, map, collector)
                /*
                if (limit!!.isAccessible(brokerServer, cMap?.get("certId") as String , this.moduleName, max.toInt(), period.toLong() )) {
                    logger.info("API Limit Not Over")
                    map?.putAll(cMap)
                    map?.put("Retry", "false")
                    this.run(this.apiUrl, map, collector)
                } else {
                    logger.info("API Limit is Over , Can not access")
                    nextError("API Limit is Over , Can not access", collector, map)
                }
                */
            }else{
                logger.info("API Limit is Over , Can not choice")
                nextError("API Limit is Over , Can not choice", collector, map)
            }
        }catch(e : Exception){
            logger.info("########## LimitManager Errro ##############")
            logger.error(e.toString(),e)
            logger.info("########## LimitManager Errro ##############")
            nextError(e.toString(), collector, map)
        }finally{
            this.async = true
        }
        return map
    }

    private fun nextError(err:String, collector: OutputCollector?, map: MutableMap<String, Any>?) {
        if (collector != null) {
            if (map != null) {
                map["ERROR"] = err
                map["Retry"] = "true"
            }
            collector.emit(Values(map))
            log(null, map)
        } else {
            logger.info("collector is null")
        }
    }

    private fun run(url: String, param: MutableMap<String, Any>?, collector: OutputCollector?) {
        logger.info("--------- APIModule run -------------")
        logger.info("url is {} , {}" , url, param)
        logger.info("paramVarMap is {}" , this.paramVarMap)
        logger.info("url is {} , paramVarMap is {} , param is {}" , url, this.paramVarMap, param)
        logger.info("--------- APIModule run -------------")

        var reqMap : MutableMap<String,Any>? = mutableMapOf()
        if(this.paramConstMap != null) reqMap?.putAll(this.paramConstMap)
        if(this.paramVarMap != null && param != null){
            this.paramVarMap.forEach { (t, u) ->
                logger.info("paramVarMap name {} , value {}" , t,u)
                param?.get(u)?.let { reqMap?.put(t, it.toString()) }
            }
        }
        log(param , null)

        var str_url = url
        var urls = url.split(",")
        var first = Random().nextInt(urls.size)
        var second = (first+1)%urls.size
        if(urls.size > 1){
            str_url = urls[first]
        }
        try {
            str_url = StringUtils.replacedAllMyBatisDollarSign(str_url,param) ?: str_url
            callApi(reqMap, param, str_url, collector)
        }catch(e:Exception){
            logger.error(e.toString())
            if(first != second) callApi(reqMap, param, urls[second], collector)
        }
    }

    private fun callApi(
        reqMap: MutableMap<String, Any>?,
        param: MutableMap<String, Any>?,
        str_url: String,
        collector: OutputCollector?
    ) {
        if ("POST" == this.method) {
            logger.info("--------- APIModule run -------------")
            logger.info("method is {} ", this.method)
            logger.info("--------- APIModule run -------------")
            var builder: FormBody.Builder = FormBody.Builder()
            if (reqMap != null) {
                reqMap?.forEach { (t, u) ->
                    builder.add(t, u.toString())
                }
            }
            logger.info("--------- APIModule run -------------")
            logger.info("parameter is ready")
            logger.info("--------- APIModule run -------------")

            val formBody: RequestBody = builder.build()
            val reqBuilder: Request.Builder = Request.Builder()
            if (this.headerConstMap != null) {
                this.headerConstMap.forEach { (t, u) ->
                    reqBuilder.addHeader(t, u.toString())
                }
            }
            if (this.headerVarMap != null) {
                this.headerVarMap.forEach { (t, u) ->
                    if (param != null) {
                        reqBuilder.addHeader(t, param[u].toString())
                    }
                }
            }
            reqBuilder.addHeader("Content-Type", "application/json;charset=UTF-8")

            logger.info("--------- APIModule run -------------")
            logger.info("header is ready")
            logger.info("--------- APIModule run -------------")

            val request: Request = reqBuilder.url(str_url).post(formBody).build()
            logger.info("--------- APIModule run -------------")
            logger.info("http body is ready")
            logger.info("--------- APIModule run -------------")

            callHttp(param, request, collector)

            logger.info("--------- APIModule run -------------")
            logger.info("call http is ok")
            logger.info("--------- APIModule run -------------")
        } else {
            logger.info("--------- APIModule run -------------")
            logger.info("method is {}", this.method)
            logger.info("query map is {}", reqMap)
            logger.info("--------- APIModule run -------------")

            val urlBuilder = str_url.toHttpUrlOrNull()!!.newBuilder()

            if (reqMap != null) {
                reqMap?.forEach { (t, u) ->
                    urlBuilder.addQueryParameter(t, u.toString())
                }
            }
            val qUrl = urlBuilder.build().toString();

            logger.info("--------- APIModule run -------------")
            logger.info("query string is {}", qUrl)
            logger.info("--------- APIModule run -------------")

            val reqBuilder: Request.Builder = Request.Builder()
            if (this.headerConstMap != null) {
                this.headerConstMap.forEach { (t, u) ->
                    reqBuilder.addHeader(t, u.toString())
                }
            }
            if (this.headerVarMap != null) {
                this.headerVarMap.forEach { (t, u) ->
                    if (param != null) {
                        reqBuilder.addHeader(t, param[u].toString())
                    }
                }
            }
            reqBuilder.addHeader("Content-Type", "application/json;charset=UTF-8")
            logger.info("--------- APIModule run -------------")
            logger.info("Get header is ready")
            logger.info("--------- APIModule run -------------")

            val request: Request = reqBuilder.url(qUrl).build()

            logger.info("--------- APIModule run -------------")
            logger.info("before get http called")
            logger.info("--------- APIModule run -------------")

            callHttp(param, request, collector)
            logger.info("--------- APIModule run -------------")
            logger.info("called get http")
            logger.info("--------- APIModule run -------------")

        }
    }

    private fun client():OkHttpClient {
        if(proxyIp != null && proxyPort != null && proxyIp != "" && proxyPort != ""){
            logger.info("--------- proxy -------------")
            logger.info("proxy ip : {} " , proxyIp)
            logger.info("proxy port : {} " , proxyPort)
            logger.info("--------- proxy -------------")
            val proxyAddr : InetSocketAddress = InetSocketAddress(proxyIp, proxyPort.toInt())
            return OkHttpClient().newBuilder().proxy(Proxy(Proxy.Type.HTTP,proxyAddr)).build()
        }else return OkHttpClient().newBuilder().connectTimeout(1800000, TimeUnit.MILLISECONDS).writeTimeout(1800000, TimeUnit.MILLISECONDS).readTimeout(1800000, TimeUnit.MILLISECONDS).build()
    }

    private fun callHttp(param: MutableMap<String, Any>?, request: Request, collector: OutputCollector?){
        client().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                logger.info("--------- http response error : {} , {} -------------" , e.toString(), call.toString())
                logger.info("Exception : {} " , e.toString())
                logger.info("Call : {} " , call.toString())
                logger.info("--------- http response error : {} -------------")
                if (collector != null) {
                    if (param != null) {
                        for (it in param) {
                            param[it.key] = it.value.toString()
                        }
                        param["error"] = e.toString()
                        param["Retry"] = "true"
                    }

                    logger.info("error before collector emit....")
                    collector.emit(Values(param))
                    logger.info("error collector emit....");
                    log(null, param)
                    logger.info("error logging... complete");
                }else{
                    logger.info("collector is null")
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val req = request.toString()
                val res = response.body?.string()
                logger.info("--------- http response -------------")
                logger.info("request : {} " , req)
                logger.info("parameter : {} " , param)
                logger.info("response : {} " , res)
                logger.info("--------- http response -------------")

                var response_type = "Map";
                if(res != null && res.trim().startsWith("[{")) response_type = "List";

                // Json 객체로 전달...
                if("JSON" == apiFormat){
                    try {
                        logger.debug("http response is json")
                        var mapType = object : TypeToken<Map<String, Any>>() {}.type
                        if ("List".equals(response_type)) mapType = object : TypeToken<List<Map<String, Any>>>() {}.type
                        logger.debug("before json to map :  {}", res)
                        if ("Map".equals(response_type)) {
                            var isList = false
                            var listOfList = mutableListOf<List<MutableMap<String, Any>?>>()
                            var map: MutableMap<String, Any>? = null
                            try {
                                map = Gson().fromJson(res, mapType)
                                if (map != null && map.isNotEmpty()) {
                                    map.forEach { (k, v) ->
                                        if (v is List<*>) {
                                            isList = true
                                            listOfList.add(v as List<MutableMap<String, Any>?>)
                                        }else{
                                            // if(!param!!.containsKey(k)) param!![k] = v
                                            param!![k] = v
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                map = mutableMapOf()
                                map["ERROR"] = e.toString()
                                if (res != null) {
                                    map["source"] = res
                                }
                            }
                            if (map == null) map = mutableMapOf()
                            // API 결과가 { a : [{K:"X' , M:'Y'},{K:"X' , M:'Y'}]} 인 경우
                            if (isList) {
                                for (list in listOfList) {
                                    list.forEach {
                                        next(it, param, collector)
                                    }
                                }
                                // API 결과가  그냥 {K:"X' , M:'Y'} 인 경우
                            } else {
                                next(map, param, collector)
                            }
                        } else {
                            var map: MutableMap<String, Any>? = null
                            // API 결과가 [{K:"X' , M:'Y'},{K:"X' , M:'Y'}] 인 경우
                            var list: List<MutableMap<String, Any>>? = null
                            try {
                                list = Gson().fromJson(res, mapType)
                                if (list != null && list.isNotEmpty()) {
                                    for (m in list) {
                                        next(m, param, collector)
                                    }
                                }
                            } catch (e: Exception) {
                                map = mutableMapOf()
                                map["ERROR"] = e.toString()
                                if (res != null) {
                                    map["source"] = res
                                }
                                next(map, param, collector)
                            }
                        }
                    }catch(ex:Exception){
                        logger.error(ex.toString(), ex)
                        var map: MutableMap<String, Any> = mutableMapOf()
                        if (map != null) {
                            map["ERROR"] = ex.toString()
                        }
                        if (res != null) {
                            map["source"] =  res
                        }
                        logger.info("APIModule Exception : {}  , because of {}" , map, ex.toString())
                        next(map, param, collector)
                    }
                }else {
                    logger.debug("http response is xml")
                    var map: MutableMap<String,Any> = mutableMapOf()
                    try {
                        val xStream = XStream(DomDriver())
                        map = xStream.fromXML(response.body?.string()) as MutableMap<String,Any>
                    }catch(e:Exception){
                        map = mutableMapOf()
                        map["ERROR"] = e.toString()
                        if (res != null) {
                            map["source"] = res
                        }
                    }
                    if(map == null) map = mutableMapOf()
                    next(map, param, collector)
                }
            }
        })
    }

    private fun next(map: MutableMap<String, Any>?, param: MutableMap<String, Any>?, collector: OutputCollector?) {
        logger.info("json to map :  {}", map)
        if(param != null && map != null){
            param?.forEach { (k, v) ->
                if (!map?.containsKey(k)!!) {
                    map?.put(k, v.toString())
                }
            }
        }
        logger.debug("parameter : {} ", param)
        if (collector != null) {
            logger.info("before collector emit....");
            logger.info("APIModule will send {} to next Bolt" , map)
            collector.emit(Values(map))
            logger.info("collector emit....");
            log(param, map)
            logger.info("logging... complete");
        } else {
            logger.info("collector is null")
        }
        logger.info("--------- http response json -------------")
    }

    override fun cleanup() {
        logger.info("##### Cleaned up....")
        if(timerTask != null) timerTask?.cancel()
        else logger.info("this module can not cancel timer....")
    }

}
