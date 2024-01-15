package com.stuko.stream.api.server.impl

import com.stuko.stream.api.buffer.AbstractLogBufferManager
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.module.IModule
import com.stuko.stream.api.module.impl.*
import com.stuko.stream.api.server.IStreamServer
import com.stuko.stream.api.server.helper.APIHelper
import com.stuko.stream.api.topology.ITopology
import com.stuko.stream.api.topology.ITopologyGroup
import com.stuko.stream.api.topology.impl.TopologyGroupImpl
import com.stuko.stream.api.topology.impl.TopologyImpl
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import kotlin.concurrent.timerTask

@Component
@DependsOn("logBufferManager")
class StreamServerImpl : IStreamServer {

    companion object : Log
    @Value("\${topology.refresh}")
    private var isRefresh:Boolean = true

    @Value("\${limit.check.ip}")
    private val limitCheckIP: String? = null
    @Value("\${limit.check.port}")
    private val limitCheckPort: String? = null

    @Value("\${api.collector.topology-group-name}")
    override lateinit var topologyGroupName: String
    // val topologyGroupName:String = "TopologyGroups"

    @Autowired
    lateinit var dao: MongoDAO
    @Autowired
    lateinit var logBufferManager: AbstractLogBufferManager
    @Autowired
    lateinit var apiHelper:APIHelper

    // @Autowired
    // @Qualifier("kafkaConsumer")
    lateinit var consumer: KafkaConsumer<Any, Any>

    private var topologyGroups:MutableMap<String, ITopologyGroup>? = mutableMapOf()

    var topologyGroupNames:String=""
        get() = field
        set(value) {
            field = value
        }
    fun loadTopologyGroup() : Unit{
        this.loadTopologyGroup(this.topologyGroupNames)
    }
    fun loadTopologyGroup(name:String) : Unit{

        val names = name.split(",")
        logger.info("########### Load Stream Server ##########")
        logger.info("TopologyGroupName : {}", name)
        logger.info("TopologyGroupName'1 : {}", names[0])
        logger.info("########### Load Stream Server ##########")


        for(n in names){

            logger.info("#### Mongo's collection name  : {}", n)
            if(dao == null)logger.info("@#$!@%#$@^^!@#%@#$^%@^$!@#$ DAO IS NULLLLLLLLLLLLLLL")
            var result:MutableList<MutableMap<String,Any>> = dao!!.find(topologyGroupName,n)
            logger.info("select from mongo's topologys : {}", result)
            var topologyGroup:ITopologyGroup = TopologyGroupImpl()
            topologyGroup.setTopologyGroupName(n)
            topologyGroups?.put(n,topologyGroup)
            result.forEach { mutableMap ->

                val topologyId: String = mutableMap["topologyId"] as String
                val topologyName: String = mutableMap["topologyName"] as String
                val nodeDataArray: MutableList<MutableMap<String, Any>> = mutableMap["nodeDataArray"] as MutableList<MutableMap<String, Any>>
                val linkDataArray: MutableList<MutableMap<String, Any>> = mutableMap["linkDataArray"] as MutableList<MutableMap<String, Any>>
                var topology:ITopology = TopologyImpl(topologyId,topologyName)

                logger.info("########### topology group info #############")
                logger.info("topologyId : {}" , topologyId);
                logger.info("topologyName : {}" , topologyName);
                logger.info("nodeDataArray : {}" , nodeDataArray.size);
                logger.info("linkDataArray : {}" , linkDataArray.size);
                logger.info("########### topology group info #############")

                nodeDataArray.forEach{node->
                    val key:String = (node["key"] as Double).toString()
                    val name:String? = if(node["name"] == null) "" else node["name"] as String
                    val count: String? = if(node["count"] == null) "1" else node["count"] as String
                    val kind: String? = if(node["kind"] == null) "" else node["kind"] as String

                    var apiUrl: String = ""
                    var apiFormat: String = "JSON"
                    var certList:MutableList<MutableMap<String,Any>> = mutableListOf()

                    if(kind != null && count != null && key != null && node["property"] != null) {
                        val property: MutableList<MutableMap<String, Any>> = node["property"] as MutableList<MutableMap<String, Any>>
                        var module: IModule? = null

                        logger.info("########### node info #############")
                        logger.info("node key : {}" , key);
                        logger.info("node name : {}" , name);
                        logger.info("node count : {}" , count);
                        logger.info("node property : {}" , property);
                        logger.info("########### node info #############")

                        // Module is API
                        // Constructor is not same....
                        if (kind == "API") {
                          val brokerServer = property.stream().filter { entry -> (entry["name"] as String) == "brokerServer" }.findFirst().get()["value"] as String
                          val numOfPartition = property.stream().filter { entry -> (entry["name"] as String) == "numOfPartition" }.findFirst().get()["value"] as String
                          val replicationFactor = property.stream().filter { entry -> (entry["name"] as String) == "replicationFactor" }.findFirst().get()["value"] as String
                          val apiKey: String = property.stream().filter { entry -> (entry["name"] as String) == "api" }.findFirst().get()["value"] as String

                          val list:MutableList<MutableMap<String,Any>> = apiHelper.getAPIInfo(apiKey)
                          if(list != null && list.size == 1){
                            apiUrl = list[0]["url"] as String
                            apiFormat = list[0]["output_format"] as String
                            certList = apiHelper.getCertList(brokerServer,numOfPartition,replicationFactor,list)
                          }
                        }
                        logger.info("##### StreameServer's LogBufferManager loading is called #####")
                        logger.info("LogBufferManager's bootstrap server is : ${this.logBufferManager.brokerServer}")
                        logger.info("##### StreameServer's LogBufferManager loading is called #####")
                        module = when (kind) {
                            "START" -> StartModule(IModule.KIND.START, key, count?.toInt(),this.logBufferManager)
                            "TCP" -> TcpModule(IModule.KIND.TCP, key, count?.toInt(),this.logBufferManager)
                            "HTTP" -> HttpModule(IModule.KIND.HTTP, key, count?.toInt(),this.logBufferManager)
                            "END" -> EndModule(IModule.KIND.END, key, count?.toInt(),this.logBufferManager)
                            "WAIT" -> WaitModule(IModule.KIND.WAIT, key, count?.toInt(),this.logBufferManager)
                            "API" -> APIModule(IModule.KIND.API, key, count?.toInt(),apiUrl, apiFormat, certList,limitCheckIP, limitCheckPort, this.logBufferManager)
                            "DBMS" -> DBMSModule(IModule.KIND.DBMS, key, count?.toInt(),this.logBufferManager)
                            "SQL" -> SQLModule(IModule.KIND.SQL, key, count?.toInt(),this.logBufferManager)
                            "MONGO" -> MongoModule(IModule.KIND.MONGO, key, count?.toInt(),this.logBufferManager)
                            "FILE" -> FileModule(IModule.KIND.FILE, key, count?.toInt(),this.logBufferManager)
                            "SELECTOR" -> SelectorModule(IModule.KIND.SELECTOR, key, count?.toInt(),this.logBufferManager)
                            "TRANSFORM" -> TransformModule(IModule.KIND.TRANSFORM, key, count?.toInt(),this.logBufferManager)
                            "FUNCTION" -> FunctionModule(IModule.KIND.FUNCTION, key, count?.toInt(),this.logBufferManager)
                            "RANGE" -> RangeModule(IModule.KIND.RANGE, key, count?.toInt(),this.logBufferManager)
                            else -> StartModule(IModule.KIND.START, key, count?.toInt(),this.logBufferManager)
                        }
                        module.db = topologyGroupName
                        module.moduleName = key
                        module.collection = n
                        module.topologyId = topologyId
                        module.setProperty(property)

                        when (module.moduleType) {
                            IModule.KIND.END -> {
                                module.setEnd(true)
                                module.init()
                                topology.addBoltModule(module)
                            }
                            IModule.KIND.START,IModule.KIND.TCP,IModule.KIND.HTTP -> {
                                module.init()
                                topology.addSpoutModule(module)
                            }
                            IModule.KIND.DBMS,IModule.KIND.SQL,IModule.KIND.API,IModule.KIND.FILE,IModule.KIND.SELECTOR,IModule.KIND.WAIT,IModule.KIND.MONGO,IModule.KIND.TRANSFORM,IModule.KIND.FUNCTION,IModule.KIND.RANGE -> topology.addBoltModule(module)
                        }
                       if(!module.start()) return
                    }
                }

                linkDataArray.forEach{link->
                  logger.info("#### link info --> from , to " , link["from"], link["to"])
                  if(link["from"] != null && link["to"] != null) {
                      val from: String = (link["from"] as Double).toString()
                      val to: String = (link["to"] as Double).toString()
                      var kind: String = (link["kind"] ?: "")as String ?: ""
                      var conditionVal:String = ""

                      logger.info("#### link foreach..... and from and to and property is not null")
                      if( link["property"]  != null ) {
                          val property: List<Map<String, String>> = link["property"] as List<Map<String, String>>
                          if (property.isNotEmpty()) {
                              for (map in property) {
                                  var name = map.get("name") as String ?: ""
                                  if ("kind" == name) kind = map["value"] as String ?: ""
                                  logger.info("#### link foreach..... name : {} , value : {}", name, map["value"])
                              }
                          }
                      }
                      if(kind == null || "" == kind) kind = "FILTER"

                      if (kind != null && "" != kind) {
                          if(link["property"] != null){
                              var mapListLink : MutableList<MutableMap<String,String>> =  link["property"] as MutableList<MutableMap<String,String>>
                              if(mapListLink.size > 0){
                                  for(mapLink : MutableMap<String,String> in mapListLink){
                                      val name = mapLink["name"] as String
                                      if("condition" == name){
                                          conditionVal = mapLink["value"] as String
                                      }
                                  }
                              }
                          }

                          val name:String = from ?: ""

                          logger.info("-------Link Info---------")
                          logger.info("{} -> {} : {} {} ", from, to, kind , conditionVal)
                          logger.info("-------Link Info---------")

                          when (kind) {
                              "FORK" -> topology.connectModule(from, to, name, conditionVal, IModule.KIND.FORK)
                              "FILTER" -> topology.connectModule(from, to, name, conditionVal, IModule.KIND.FILTER)
                              "LINK" -> topology.connectModule(from, to, name, conditionVal, IModule.KIND.LINK)
                              else -> topology.connectModule(from, to, name, conditionVal, IModule.KIND.LINK)
                          }
                      } else {
                          logger.info("-------Link Info---------")
                          logger.info("Kind info is NULL")
                          logger.info("-------Link Info---------")
                      }
                  }
                }
                if(topology?.topologyId != null){
                    logger.info("------- Topology Execute Status ---------")
                    logger.info(" Topology Execute Status is Ready")
                    logger.info(" Topology {} will be executed", topology.topologyId)
                    logger.info("------- Topology Execute Status ---------")

                    topologyGroup.getTopologyMap()[topology.topologyId] = topology
                    topology.executeModules()
                }else{
                    logger.info("------- Topology Execute Status ---------")
                    logger.info(" Topology Execute Status is NOT OK")
                    logger.info("------- Topology Execute Status ---------")
                }
            }
        }
    }

    override fun start(topologyGroupName:String) {
        // this.isRefresh = false
        this.topologyGroupNames = topologyGroupName
        this.loadTopologyGroup(this.topologyGroupNames)
        // startTopologyRefreshConsumer()
    }

    override fun stop() {
        logger.info("---- stop topology ----")
        topologyGroups?.forEach { (k, v) ->
            logger.info("{} is stopping...", k)
            try{v.stopAllTopology()}catch(e:Exception){logger.error(e.toString(), e)}
            logger.info("{} is stopped", k)
        }
        logger.info("---- stop topology ----")
    }

    override fun status(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    override fun getTopologyGroup(topologyGroupName: String): ITopologyGroup? {
        return this.topologyGroups?.get(topologyGroupName)
    }

    fun startTopologyRefreshConsumer():Unit{
        if(this.consumer != null) {
            Timer(true).scheduleAtFixedRate(timerTask {
                logger.info("Check Refresh");
                if(isRefresh) {
                    logger.info("Refresh will be executed.");
                    checkRefreshAndStop()
                }
            },1000, 10000)
            /*
            // 카프카 Consumer 는 Thread Safe(is not safe for multi-threaded access) 하지 않음.
            GlobalScope.launch {
                while(!isRefresh) {
                    checkRefreshAndStop();
                }
            }
            */
        }
    }

    fun checkRefreshAndStop(): Unit{
        try {
            var records: ConsumerRecords<Any, Any> = consumer!!.poll(Duration.ofMillis(1000));
            if(records != null) {
                for (record: ConsumerRecord<Any, Any> in records) {
                    var json: String = record.value() as String
                    logger.info("#########################################")
                    logger.info("#########################################")
                    logger.info("#### Logging data is ${json}")
                    logger.info("#########################################")
                    logger.info("#########################################")

                    // val map = ObjectMapper().readValue<MutableMap<String, Any>?>(json) ?: continue
                    if (json == "refresh") {
                        logger.info("All of topology will be reloaded....")
                        stop()
                        this.loadTopologyGroup(this.topologyGroupNames)
                        logger.info("All of topology is reloaded....")
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e.toString(),e)
        } finally {
        }
    }
}