package com.stuko.stream.api.topology.impl

import com.stuko.stream.api.common.Log
import com.stuko.stream.api.module.IModule
import com.stuko.stream.api.module.impl.*
import com.stuko.stream.api.topology.ITopology
import com.stuko.stream.api.topology.grouping.LogicalGrouping
import org.apache.storm.Config
import org.apache.storm.LocalCluster
import org.apache.storm.StormSubmitter
import org.apache.storm.topology.BoltDeclarer
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.SpoutDeclarer
import org.apache.storm.topology.TopologyBuilder
import org.springframework.stereotype.Component


class TopologyImpl(override var topologyId: String, override var topologyName: String) : ITopology {

    companion object : Log
    // private var localTopologyBuilder:LinearDRPCTopologyBuilder = LinearDRPCTopologyBuilder(topologyId)
    private var topologyBuilder:TopologyBuilder = TopologyBuilder()
    private var boltMap:MutableMap<String, BoltDeclarer> = mutableMapOf()
    private var spoutMap:MutableMap<String, SpoutDeclarer> = mutableMapOf()
    private var clusterEnv:Boolean = false

    override fun addBoltModule(module: IModule) {
        var declarer: OutputFieldsDeclarer? = null
        var boltDeclarer: BoltDeclarer? = null
        boltDeclarer= when(module.moduleType){
            IModule.KIND.END->topologyBuilder.setBolt(module.moduleKey, module as EndModule, module.moduleCount)
            IModule.KIND.DBMS->topologyBuilder.setBolt(module.moduleKey, module as DBMSModule, module.moduleCount)
            IModule.KIND.SQL->topologyBuilder.setBolt(module.moduleKey, module as SQLModule, module.moduleCount)
            IModule.KIND.FILE->topologyBuilder.setBolt(module.moduleKey, module as FileModule, module.moduleCount)
            IModule.KIND.WAIT->topologyBuilder.setBolt(module.moduleKey, module as WaitModule, module.moduleCount)
            IModule.KIND.API->topologyBuilder.setBolt(module.moduleKey, module as APIModule, module.moduleCount)
            IModule.KIND.MONGO->topologyBuilder.setBolt(module.moduleKey, module as MongoModule, module.moduleCount)
            IModule.KIND.SELECTOR->topologyBuilder.setBolt(module.moduleKey, module as SelectorModule, module.moduleCount)
            IModule.KIND.TRANSFORM->topologyBuilder.setBolt(module.moduleKey, module as TransformModule, module.moduleCount)
            IModule.KIND.RANGE->topologyBuilder.setBolt(module.moduleKey, module as RangeModule, module.moduleCount)
            IModule.KIND.FUNCTION->topologyBuilder.setBolt(module.moduleKey, module as FunctionModule, module.moduleCount)
            else->topologyBuilder.setBolt(module.moduleName, module as EndModule, module.moduleCount)
        }
        this.boltMap[module.moduleKey] = boltDeclarer
    }

    override fun addSpoutModule(module: IModule) {
        var declarer: OutputFieldsDeclarer? = null
        var spoutDeclarer:SpoutDeclarer? = null
        spoutDeclarer = when(module.moduleType){
            IModule.KIND.START->topologyBuilder.setSpout(module.moduleKey, module as StartModule, module.moduleCount)
            IModule.KIND.HTTP->topologyBuilder.setSpout(module.moduleKey, module as HttpModule, module.moduleCount)
            IModule.KIND.TCP->topologyBuilder.setSpout(module.moduleKey, module as TcpModule, module.moduleCount)
            else->topologyBuilder.setSpout(module.moduleKey, module as StartModule, module.moduleCount)
        }
        this.spoutMap[module.moduleKey] = spoutDeclarer
    }


    override fun connectModule(from: String, to: String, name:String, condition:String , kind: IModule.KIND) {
        logger.info("------- connect module -------")
        logger.info(" from {} -> to {} : kind {}", from, to, kind)
        logger.info("------- connect module -------")
        if(name == null || "" == name || condition == null || "" == condition){
            this.boltMap[to]?.shuffleGrouping(from)
        }else this.boltMap[to]?.customGrouping(from,LogicalGrouping(condition))

        /*
        if(this.spoutMap.containsKey(from)){ // Spout 인경우
            this.boltMap.get(to)?.shuffleGrouping(from)
        }else{ // Bolt 인경우
            // this.boltMap.get(to)?.allGrouping(from)
            if(name == null || "".equals(name) || condition == null || "".equals(condition))this.boltMap.get(to)?.allGrouping(from)
            else this.boltMap.get(to)?.customGrouping(name,LogicalGrouping(condition))
        }
       */
    }

    override fun executeModules() {

      var conf:Config = Config()
      // conf[Config.BLOBSTORE_DIR] = "/tmp/storm/local/dir/blobstore"
      // conf[Config.STORM_LOCAL_DIR] = "/tmp/storm/local/dir"
      // conf[Config.NIMBUS_THRIFT_MAX_BUFFER_SIZE] = 20480000
      // conf.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer::class.java)
      // conf.registerMetricsConsumer()
      // var workerMetrics:MutableMap<String,String> = mutableMapOf()
      // workerMetrics["CPU"] = "org.apache.storm.metrics.sigar.CPUMetric"
      // conf[Config.TOPOLOGY_WORKER_METRICS] = workerMetrics
      // conf.setTopologyStrategy("org.apache.storm.scheduler.resource.strategies.scheduling.DefaultResourceAwareStrategy")
      // conf.setTopologyStrategy(org.apache.storm.scheduler.resource.strategies.scheduling.DefaultResourceAwareStrategy.class)
      // conf.setTopologyStrategy("org.apache.storm.scheduler.resource.strategies.scheduling.DefaultResourceAwareStrategy")

      // conf.setDebug(true)
      try {
          if (clusterEnv) {
              // conf[Config.TOPOLOGY_WORKER_SHARED_THREAD_POOL_SIZE] = "500"
              // conf.setNumWorkers(50);
              conf.setNumWorkers(3)
              StormSubmitter.submitTopology(this.topologyName, conf, topologyBuilder.createTopology())
          } else {
              // val drpc = LocalDRPC()
              // conf.setMaxTaskParallelism(3);
              // conf.setNumWorkers(200)
              // conf.setMaxTaskParallelism(3)
              // ClusterObject.cluster.submitTopology(this.topologyName, conf, topologyBuilder.createTopology())
              // cluster = LocalCluster()
              // conf.setNumWorkers(50)
              ClusterObject.cluster?.submitTopology(this.topologyName, conf, topologyBuilder.createTopology())
              // cluster.submitTopology(this.topologyName,conf,localTopologyBuilder.createLocalTopology(drpc))
          }
      }catch(e:Exception){
          logger.error("---- Load Storm Cluster Error ----")
          logger.error(e.toString())
          logger.error("---- Load Storm Cluster Error ----")
      }

    }

    override fun stopModules() {
        try {
            // if(cluster != null)cluster?.killTopology(this.topologyName)
        }catch(e:Exception){
            logger.error("---- Shutdown Storm Cluster Error ----")
            logger.error(e.toString())
            logger.error("---- Shutdown Storm Cluster Error ----")
        }
        initTopology()
    }

    private fun initTopology(){
        topologyBuilder = TopologyBuilder()
        boltMap = mutableMapOf()
        spoutMap = mutableMapOf()
        clusterEnv = false
    }
}

@Component
object ClusterObject:StormLocalCluster() {
}

open class StormLocalCluster{
    public var cluster:LocalCluster = LocalCluster()
}