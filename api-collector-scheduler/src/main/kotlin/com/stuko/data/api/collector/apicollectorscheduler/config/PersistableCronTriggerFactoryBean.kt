package com.stuko.data.api.collector.apicollectorscheduler.config

import org.springframework.scheduling.quartz.CronTriggerFactoryBean

class PersistableCronTriggerFactoryBean: CronTriggerFactoryBean() {

    val JOB_DETAIL_KEY:String = "jobDetail"

    override fun afterPropertiesSet() {
        super.afterPropertiesSet()

        jobDataMap.remove(JOB_DETAIL_KEY)
    }

}