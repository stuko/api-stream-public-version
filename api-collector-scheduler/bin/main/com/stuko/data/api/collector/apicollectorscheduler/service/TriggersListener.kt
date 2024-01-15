package com.stuko.data.api.collector.apicollectorscheduler.service

import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import org.quartz.JobExecutionContext
import org.quartz.Trigger
import org.quartz.TriggerListener
import org.springframework.stereotype.Component

@Component
class TriggersListener : TriggerListener {

    companion object:Log

    override fun triggerFired(trigger: Trigger?, context: JobExecutionContext?) {
        log.info("triggerFired")
        log.info("trigger : {} is fired", context?.jobDetail?.key.toString())
    }

    override fun getName(): String="globalTrigger"

    override fun vetoJobExecution(trigger: Trigger?, context: JobExecutionContext?): Boolean {
        log.info("TriggersListener.vetoJobExecution()")
        return false
    }

    override fun triggerComplete(trigger: Trigger?, context: JobExecutionContext?, triggerInstructionCode: Trigger.CompletedExecutionInstruction?) {
        log.info("TriggersListener.triggerComplete()")
        val jobName = trigger!!.jobKey.name
        log.info("Job name: {},  trigger: {}  completed at {}", jobName, trigger.jobKey, trigger.startTime)
    }

    override fun triggerMisfired(trigger: Trigger?) {
        log.info("TriggerListener.triggerMisfired()")
        log.info("Job name: {},  trigger: {}  misfired at {}", trigger?.jobKey?.name, trigger?.jobKey, trigger?.startTime)
    }
}