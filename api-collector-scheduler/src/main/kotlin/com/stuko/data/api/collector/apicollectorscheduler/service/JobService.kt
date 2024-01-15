package com.stuko.data.api.collector.apicollectorscheduler.service

import org.springframework.scheduling.quartz.QuartzJobBean
import java.util.*

interface JobService {
    fun scheduleOneTimeJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, topicName:String): Boolean
    fun scheduleCronJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, cronExpression: String, topicName:String): Boolean
    fun updateOneTimeJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, topicName:String): Boolean
    fun updateCronJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, cronExpression: String, topicName:String): Boolean
    fun unScheduleJob(jobName: String): Boolean
    fun deleteJob(jobName: String): Boolean
    fun pauseJob(jobName: String): Boolean
    fun resumeJob(jobName: String): Boolean
    fun startJobNow(jobName: String): Boolean
    fun isJobRunning(jobName: String): Boolean
    fun getAllJobs():List<MutableMap<String, Any?>>
    fun isJobWithNamePresent(jobName: String): Boolean
    fun getJobState(jobName: String): String
    fun stopJob(jobName: String): Boolean
}