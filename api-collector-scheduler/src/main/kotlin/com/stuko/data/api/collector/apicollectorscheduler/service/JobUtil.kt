package com.stuko.data.api.collector.apicollectorscheduler.service

import com.stuko.data.api.collector.apicollectorscheduler.config.PersistableCronTriggerFactoryBean
import org.quartz.*
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
import java.text.ParseException
import java.util.*

class JobUtil {

    fun createJob(jobClass: Class<out QuartzJobBean>, isDurable: Boolean, context: ApplicationContext
                  , jobName: String, jobGroup: String, topicName: String): JobDetail? {
        val factoryBean = JobDetailFactoryBean()
        factoryBean.setJobClass(jobClass)
        factoryBean.setDurability(isDurable)
        factoryBean.setApplicationContext(context)
        factoryBean.setName(jobName)
        factoryBean.setGroup(jobGroup)

        val jobDataMap = JobDataMap()
        jobDataMap["topicName"] = topicName
        factoryBean.setJobDataAsMap(jobDataMap)
        factoryBean.afterPropertiesSet()

        return factoryBean.`object`
    }

    fun createCronTrigger(triggerName: String, startTime: Date, cronExpression: String, misFireInstruction: Int): Trigger? {
        val factoryBean = PersistableCronTriggerFactoryBean()
        factoryBean.setName(triggerName)
        factoryBean.setStartTime(startTime)
        factoryBean.setCronExpression(cronExpression)
        factoryBean.setMisfireInstruction(misFireInstruction)

        try {
            factoryBean.afterPropertiesSet()
        }catch (e: ParseException){
            e.printStackTrace()
        }

        return factoryBean.`object`
    }

    fun createSingleTrigger(triggerName: String, startTime: Date, misFireInstruction: Int): Trigger?{
        val factoryBean = SimpleTriggerFactoryBean()
        factoryBean.setName(triggerName)
        factoryBean.setStartTime(startTime)
        factoryBean.setMisfireInstruction(misFireInstruction)
        factoryBean.setRepeatCount(0)
        factoryBean.afterPropertiesSet()

        return factoryBean.`object`
    }
}