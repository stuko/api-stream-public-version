package com.stuko.data.api.collector.apicollectorscheduler.service.impl

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import com.stuko.data.api.collector.apicollectorscheduler.service.JobService
import com.stuko.data.api.collector.apicollectorscheduler.service.JobUtil
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class JobServiceImpl: JobService {

    @Autowired
    @Lazy
    lateinit var schedulerFactoryBean: SchedulerFactoryBean

    @Autowired
    lateinit var context: ApplicationContext

    companion object:Log

    override fun scheduleOneTimeJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, topicName: String): Boolean {
        log.debug("Request received to scheduleJob")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"
        val triggerKey: String = jobName

        val jobDetail: JobDetail? = JobUtil().createJob(jobClass, false, context, jobKey, groupKey, topicName)
        log.info("creating trigger for key :$jobKey at date :$date")
        val simpleTriggerBean: Trigger? = JobUtil().createSingleTrigger(triggerKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)

        return try {
            val scheduler:Scheduler = schedulerFactoryBean.scheduler
            val dt:Date = scheduler.scheduleJob(jobDetail, simpleTriggerBean)
            log.info("Job with key jobKey :$jobKey and group :$groupKey scheduled successfully for date :$dt")
            true
        }catch (e:SchedulerException){
            log.info("SchedulerException while scheduling job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }

    }

    override fun scheduleCronJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, cronExpression: String, topicName:String): Boolean {
        log.debug("Request received to scheduleJob")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"
        val triggerKey: String = jobName

        val jobDetail: JobDetail? = JobUtil().createJob(jobClass, false, context, jobKey, groupKey, topicName)
        log.info("creating cron trigger for key :$jobKey at date: $date")
        val cronTriggerBean:Trigger? = JobUtil().createCronTrigger(triggerKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)

        return try {
            val scheduler:Scheduler = schedulerFactoryBean.scheduler
            val dt:Date = scheduler.scheduleJob(jobDetail, cronTriggerBean)
            log.info("Job with key jobKey :$jobKey and group :$groupKey scheduled successfully for date :$dt")
            true
        }catch (e:SchedulerException){
            log.info("SchedulerException while scheduling job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }

    }

    override fun updateOneTimeJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, topicName:String): Boolean {
        log.debug("Request received for updating one time job.")

        val jobKey:String = jobName
        val groupKey = "SampleGroup"

        log.info("Parameters received for updating one time job : jobKey :$jobKey, date: $date")
        return try {

            val jobDetail: JobDetail? = JobUtil().createJob(jobClass, false, context, jobKey, groupKey, topicName)
            val newTrigger:MutableSet<Trigger?> = mutableSetOf(JobUtil().createSingleTrigger(jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW))
            val scheduler:Scheduler = schedulerFactoryBean.scheduler

            val dt: Unit = scheduler.scheduleJob(jobDetail, newTrigger, true)
            log.info("Trigger associated with jobKey :$jobKey rescheduled successfully for date :$dt")
            true
        }catch (e:Exception){
            log.info("SchedulerException while updating one time job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }

    }

    override fun updateCronJob(jobName: String, jobClass: Class<out QuartzJobBean>, date: Date, cronExpression: String, topicName:String): Boolean {
        log.debug("Request received for updating cron job.")
        val jobKey:String = jobName
        val groupKey = "SampleGroup"

        log.info("Parameters received for updating cron job : jobKey :$jobName, date: $date")
        return try {

            val jobDetail: JobDetail? = JobUtil().createJob(jobClass, false, context, jobKey, groupKey, topicName)
            val newTrigger: MutableSet<Trigger?> = mutableSetOf(JobUtil().createCronTrigger(jobKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW))
            val scheduler:Scheduler = schedulerFactoryBean.scheduler

            val dt: Unit = scheduler.scheduleJob(jobDetail, newTrigger, true)
            log.info("Trigger associated with jobKey :$jobKey rescheduled successfully for date :$dt")
            true
        }catch (e:Exception) {
            log.info("SchedulerException while updating cron job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun unScheduleJob(jobName: String): Boolean {
        log.debug("Request received for UnScheduleding job.")

        val jobKey: String = jobName

        val tkey = TriggerKey(jobKey)
        log.info("Parameters received for unscheduling job : tkey :$jobKey")
        return try {
            val status: Boolean = schedulerFactoryBean.scheduler.unscheduleJob(tkey)
            log.info("Trigger associated with jobKey :$jobKey unscheduled with status :$status")
            status
        } catch (e:SchedulerException) {
            log.info("SchedulerException while unscheduling job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun deleteJob(jobName: String): Boolean {
        log.debug("Request received for deleting job.")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"

        val jkey = JobKey(jobKey, groupKey)
        log.info("Parameters received for deleting job : jobKey :$jobKey")

        return try {
            val status: Boolean = schedulerFactoryBean.scheduler.deleteJob(jkey)
            log.info("Job with jobKey :$jobKey deleted with status :$status")
            status
        } catch (e:SchedulerException) {
            log.info("SchedulerException while deleting job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun pauseJob(jobName: String): Boolean {
        log.debug("Request received for pausing job.")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"
        val jkey = JobKey(jobKey, groupKey)
        log.info("Parameters received for pausing job : jobKey :$jobKey, groupKey :$groupKey")

        return try {
            schedulerFactoryBean.scheduler.pauseJob(jkey)
            log.info("Job with jobKey :$jobKey paused succesfully.")
            true
        } catch (e:SchedulerException) {
            log.info("SchedulerException while pausing job with key :" + jobName + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun resumeJob(jobName: String): Boolean {
        log.debug("Request received for resuming job.")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"

        val jkey = JobKey(jobKey, groupKey)
        log.info("Parameters received for resuming job : jobKey :$jobKey")
        return try {
            schedulerFactoryBean.scheduler.resumeJob(jkey)
            log.info("Job with jobKey :$jobKey resumed succesfully.")
            true
        } catch (e:SchedulerException) {
            log.info("SchedulerException while resuming job with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun startJobNow(jobName: String): Boolean {
        log.debug("Request received for starting job now.")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"

        val jkey = JobKey(jobKey, groupKey)
        log.info("Parameters received for starting job now : jobKey :$jobKey")
        return try {
            schedulerFactoryBean.scheduler.triggerJob(jkey)
            log.info("Job with jobKey :$jobKey started now succesfully.")
            true
        } catch (e:SchedulerException) {
            log.info("SchedulerException while starting job now with key :" + jobKey + " message :" + e.message)
            e.printStackTrace()
            false
        }
    }

    override fun isJobRunning(jobName: String): Boolean {
        log.debug("Request received to check if job is running")

        val jobKey: String = jobName
        val groupKey = "SampleGroup"

        log.debug("Parameters received for checking job is running now : jobKey :$jobKey")

        try {
            val currentJobs:List<JobExecutionContext>? = schedulerFactoryBean.scheduler.currentlyExecutingJobs
            if (currentJobs!=null){
                for(jobCtx: JobExecutionContext in currentJobs){
                    val jobNameDB: String = jobCtx.jobDetail.key.name
                    val groupNameDB: String = jobCtx.jobDetail.key.group
                    if (jobKey.equals(jobNameDB,true)&& groupKey.equals(groupNameDB, true)){
                        return true
                    }
                }
            }
        } catch (e:SchedulerException){
            log.debug("SchedulerException while checking job with key :" + jobKey + " is running. error message :" + e.message)
            e.printStackTrace()
            return false
        }
        return false
    }

    override fun getAllJobs(): List<MutableMap<String, Any?>> {
        val list = ArrayList<MutableMap<String, Any?>>()
        try {
            val scheduler:Scheduler = schedulerFactoryBean.scheduler
            val instanceId = scheduler.schedulerInstanceId

            for(groupName:String in scheduler.jobGroupNames){
                for(jobKey:JobKey in scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))){
                    val jobName:String = jobKey.name
                    val jobGroup:String = jobKey.group

                    val triggers:List<Trigger> = scheduler.getTriggersOfJob(jobKey)
                    val scheduleTime:Date = triggers[0].startTime
                    val nextFireTime:Date = triggers[0].nextFireTime
                    val lastFiredTime:Date? = triggers[0].previousFireTime
                    val map: MutableMap<String, Any?> = HashMap()
                    map["jobName"] = jobName
                    map["groupName"] = jobGroup
                    map["scheduleTime"] = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(scheduleTime)
                    map["lastFiredTime"] = if (lastFiredTime != null) {
                        SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(lastFiredTime)
                    }else{
                        ""
                    }
                    map["nextFireTime"] = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(nextFireTime)
                    map["instanceId"] = instanceId
                    map["status"] = getJobState(jobName)
                    map["topicName"] = scheduler.getJobDetail(jobKey).jobDataMap["topicName"]
                    map["jobStatus"] = if (isJobRunning(jobName)){
                                             "RUNNING"
                                        }else {
                                            getJobState(jobName)
                                        }
                    if(triggers[0] is CronTrigger){
                        val cronTrigger:CronTrigger = triggers[0] as CronTrigger
                        map["cronExpression"] = cronTrigger.cronExpression
                    }
                    list.add(map)
                }
            }
        }catch (e:SchedulerException){
            log.info("SchedulerException while fetching all jobs. error message :" + e.message)
            e.printStackTrace()
        }

        return list
    }

    override fun isJobWithNamePresent(jobName: String): Boolean {
        try {
            val groupKey = "SampleGroup"
            val jobKey = JobKey(jobName, groupKey)
            val scheduler = schedulerFactoryBean.scheduler
            if (scheduler.checkExists(jobKey)) {
                return true
            }
        } catch (e:SchedulerException) {
            log.info("SchedulerException while checking job with name and group exist:" + e.message)
            e.printStackTrace()
        }
        return false
    }

    override fun getJobState(jobName: String): String {
        log.debug("JobServiceImpl.getJobState()")

        try {
            val groupKey = "SampleGroup"
            val jobKey = JobKey(jobName, groupKey)

            val scheduler = schedulerFactoryBean.scheduler
            val jobDetail = scheduler.getJobDetail(jobKey)

            val triggers: List<Trigger> = scheduler.getTriggersOfJob(jobDetail.key)
            if (triggers.isNotEmpty()) {
                for (trigger:Trigger in triggers) run {
                    return when(scheduler.getTriggerState(trigger.key)){
                                    Trigger.TriggerState.PAUSED -> "PAUSED"
                                    Trigger.TriggerState.BLOCKED -> "BLOCKED"
                                    Trigger.TriggerState.COMPLETE -> "COMPLETE"
                                    Trigger.TriggerState.ERROR -> "ERROR"
                                    Trigger.TriggerState.NONE -> "NONE"
                                    Trigger.TriggerState.NORMAL -> "NORMAL"
                                    else -> "NONE"
                            }
                }
            }
        } catch (e:SchedulerException) {
            log.info("SchedulerException while checking job with name and group exist:" + e.message)
            e.printStackTrace()
        }
        return "NONE"
    }

    override fun stopJob(jobName: String): Boolean {
        log.debug("JobServiceImpl.stopJob()")
        try {
            val jobKey: String = jobName
            val groupKey = "SampleGroup"

            val scheduler: Scheduler = schedulerFactoryBean.scheduler
            val jkey = JobKey(jobKey, groupKey)

            return scheduler.interrupt(jkey)

        } catch (e:SchedulerException) {
            log.info("SchedulerException while stopping job. error message :" + e.message)
            e.printStackTrace()
        }
        return false
    }
}