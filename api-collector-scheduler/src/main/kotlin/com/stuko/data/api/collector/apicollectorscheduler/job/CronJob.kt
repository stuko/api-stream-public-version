package com.stuko.data.api.collector.apicollectorscheduler.job

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import com.stuko.data.api.collector.apicollectorscheduler.service.JobService
import com.stuko.data.api.collector.apicollectorscheduler.service.KafkaProducer
import org.quartz.InterruptableJob
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.util.*

@Component
class CronJob : QuartzJobBean(), InterruptableJob{

    private var isJobInterrupted: Boolean = true
    @Volatile private lateinit var currThread: Thread

    @Autowired
    lateinit var jobService: JobService

    @Autowired
    lateinit var producer: KafkaProducer

    companion object: Log

    override fun executeInternal(jobExecutionContext: JobExecutionContext) {
        currThread = Thread.currentThread()
        log.info("")
        log.info("======================================")
        val key = jobExecutionContext.jobDetail.key
        log.info("Cron Job started with key :" + key.name + ", Group :" + key.group + " , Thread Name :" + currThread.name
                + " ,Time now :" + Date())
        log.info("Accessing annotation example: " + jobService.getAllJobs())
        val list = jobService.getAllJobs()
        log.info("Job List : $list")

        //*********** For retrieving stored key-value pairs ***********/
        val datamap: JobDataMap = jobExecutionContext.mergedJobDataMap
        val topicName = datamap.getString("topicName")
        log.info("topicName: $topicName")
        log.info("jobName: $key.name")

        try{
            producer.sendMessage(topicName,null, key.name)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }finally {
            if(isJobInterrupted){
                log.info("Job " + key.name + " did not complete")
            }else{
                log.info("Job " + key.name + " completed at " + Date())
            }
        }

        log.info("Thread: " + Thread.currentThread().name + " stopped.")
        log.info("======================================")
        log.info("")
    }

    override fun interrupt() {
        log.info("Interrupted thread... ")
        isJobInterrupted = true
        currThread.interrupt()
    }
}