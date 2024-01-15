package com.stuko.stream.api.buffer

import com.stuko.stream.api.common.KafkaUtils
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

abstract class AbstractLogBuffer : Serializable{
    public var brokerServer:String = ""
    public var topicName:String = ""
    public var waitTime:Long = 10000
    public var Max:Int = 500
    public var MaxUnit:Int = 1000*60*60*24
    public var partition:Int = 1
    public var replication:Int = 1
    public var debug = "false"
    fun init(brokerServer:String, topicName:String, max:Int , partition:Int, replication:Int , debug:String){
        this.brokerServer = brokerServer
        this.topicName = topicName
        this.MaxUnit = max
        this.partition = partition
        this.replication = replication
        this.debug = debug
        KafkaUtils.createTopic(KafkaUtils.getProperties("admin" , this.brokerServer ,  true) , this.topicName , this.partition,  this.replication )
        this.createKafka()
    }
    abstract fun createKafka()
}

abstract class AbstractLogBufferManager : AbstractLogBuffer(){
    abstract fun write(json:String)
}

abstract class AbstractLogBufferConsumer : AbstractLogBuffer(){
    abstract fun start()
}

