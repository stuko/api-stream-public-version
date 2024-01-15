package com.stuko.data.api.collector.apicollectorscheduler.config

import org.quartz.JobListener
import org.quartz.TriggerListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.boot.autoconfigure.quartz.QuartzProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import java.util.*
import javax.sql.DataSource

@Configuration
class QuartzSchedulerConfig {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var quartzProperties: QuartzProperties

    @Autowired
    lateinit var triggerListener: TriggerListener

    @Autowired
    lateinit var jobListener: JobListener

    @Bean
    fun schedulerFactoryBean():SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        factory.setOverwriteExistingJobs(true)
        factory.setDataSource(dataSource)
        factory.setQuartzProperties(quartzProperties.properties.toProperties())

        factory.setGlobalTriggerListeners(triggerListener)
        factory.setGlobalJobListeners(jobListener)

        val jobFactory = AutowiringSpringBeanJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        factory.setJobFactory(jobFactory)

        return factory

    }

}