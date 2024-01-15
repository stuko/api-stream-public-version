package com.stuko.data.api.collector.apicollectorscheduler.job

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import com.stuko.data.api.collector.apicollectorscheduler.service.JobService
import com.stuko.data.api.collector.apicollectorscheduler.service.KafkaProducer
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class SimpleJob: QuartzJobBean(), InterruptableJob {

    @Volatile private var toStopFlag: Boolean = true

    @Autowired
    lateinit var jobService: JobService

    @Autowired
    lateinit var producer: KafkaProducer

    companion object: Log

    override fun executeInternal(jobExecutionContext: JobExecutionContext) {
        val key = jobExecutionContext.jobDetail.key
        log.info(
                "Simple Job started with key :" + key.name + ", Group :" + key.group + " , Thread Name :" + Thread.currentThread().name)

        log.info("======================================")
        log.info("Accessing annotation example: " + jobService.getAllJobs())
        val list = jobService.getAllJobs()
        log.info("Job list :$list")
        log.info("======================================")

        //*********** For retrieving stored key-value pairs ***********/
        val dataMap = jobExecutionContext.mergedJobDataMap
        val topicName = dataMap.getString("topicName")
        log.info("topologyId: $topicName")

        while (toStopFlag){
            try{
                producer.sendMessage(topicName,null, "message")
//            log.info("producerSendMessage topicName:$topologyId, message:message")
                log.info("Job Running... Thread Name :" + Thread.currentThread().name)
                TimeUnit.SECONDS.sleep(10)
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }
        log.info("Thread: " + Thread.currentThread().name + " stopped.")
    }

    override fun interrupt() {
        log.info("Stopping thread... ")
        toStopFlag = false
    }
}