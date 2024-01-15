package com.stuko.data.api.collector.apicollectorscheduler.service

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.springframework.stereotype.Component

@Component
class JobsListener : JobListener {

    companion object : Log

    override fun getName(): String = "globalJob"


    override fun jobToBeExecuted(context: JobExecutionContext?) {
        log.info("jobToBeExecuted : {}",context?.jobDetail)
        log.info("Job : {} is going to start ...", context?.jobDetail?.key.toString())

    }

    override fun jobWasExecuted(context: JobExecutionContext?, jobException: JobExecutionException?) {
        log.info("JobsListener.jobWasExecuted()")
        log.info("Job : {} is finished...", context?.jobDetail?.key.toString())

    }

    override fun jobExecutionVetoed(context: JobExecutionContext?) {
        log.info("JobsListener.jobExecutionVetoed() : {}", context?.jobDetail)
    }
}