package com.stuko.data.api.collector.apicollectorscheduler.controller

import com.google.gson.Gson
import com.stuko.data.api.collector.apicollectorscheduler.common.Log
import com.stuko.data.api.collector.apicollectorscheduler.job.CronJob
import com.stuko.data.api.collector.apicollectorscheduler.job.SimpleJob
import com.stuko.data.api.collector.apicollectorscheduler.service.JobService
import com.stuko.data.api.collector.apicollectorscheduler.service.KafkaProducer
import com.stuko.data.api.collector.apicollectorscheduler.util.ServerResponseCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/scheduler/")
class JobController {

    val logger: Logger get() = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    @Lazy
    lateinit var jobService: JobService

    val gson = Gson()

    companion object: Log

    @Autowired
    lateinit var producer: KafkaProducer

    @RequestMapping("schedule", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun schedule(@RequestBody requestBody: MutableMap<String, String>):String {
        log.info("JobController.schedule()")

        val jobName = requestBody["jobName"]?:""
        val scheduleTime: Date = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(requestBody["scheduleTime"])
        val cronExpression = requestBody["cronExpression"]?:""
        val topicName = requestBody["topicName"]?:""
        log.debug("JobController.schedule()")

        if(jobName.trim() == ""){
            return getServerResponse(ServerResponseCode().JOB_NAME_NOT_PRESENT, false)
        }else if(topicName.trim() == ""){
            return getServerResponse(ServerResponseCode().TOPIC_NAME_NOT_PRESENT, false)
        }

        return if (!jobService.isJobWithNamePresent(jobName)){
                    if(when(cronExpression.trim() == ""){
                                true->jobService.scheduleOneTimeJob(jobName, SimpleJob().javaClass, scheduleTime, topicName)
                                else -> jobService.scheduleCronJob(jobName, CronJob().javaClass, scheduleTime, cronExpression, topicName)}){
                        getServerResponse(ServerResponseCode().SUCCESS, jobService.getAllJobs())
                    }else{
                        getServerResponse(ServerResponseCode().ERROR, false)
                    }
                }else{
                    getServerResponse(ServerResponseCode().JOB_WITH_SAME_NAME_EXIST, false)
                }
    }

    @RequestMapping("unschedule", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun unschedule(@RequestBody requestBody: MutableMap<String, String>): String{
        log.info("JobController.unschedule()")

        val jobName = requestBody["jobName"]?:""

        return if(jobName.trim() == ""){
            getServerResponse(ServerResponseCode().JOB_NAME_NOT_PRESENT, false)
        }else{
            jobService.unScheduleJob(jobName)
            getServerResponse(ServerResponseCode().SUCCESS, true)
        }
    }

    @RequestMapping("delete", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun delete(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.delete()")
        val jobName = requestBody["jobName"]?:""
        return if (jobService.isJobWithNamePresent(jobName)){
                    if (!jobService.isJobRunning(jobName)){
                        if (jobService.deleteJob(jobName)){
                            getServerResponse(ServerResponseCode().SUCCESS, true)
                        }else{
                            getServerResponse(ServerResponseCode().ERROR, false)
                        }
                    }else{
                        getServerResponse(ServerResponseCode().JOB_ALREADY_IN_RUNNING_STATE, false)
                    }
                }else{
                    getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST,false)
                }
    }

    @RequestMapping("pause", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun pause(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.pause()")
        val jobName = requestBody["jobName"]?:""
        return if(jobService.isJobWithNamePresent(jobName)){
                    if(!jobService.isJobRunning(jobName)){
                        if(jobService.pauseJob(jobName)){
                            getServerResponse(ServerResponseCode().SUCCESS, true)
                        }else{
                            getServerResponse(ServerResponseCode().ERROR, false)
                        }
                    }else{
                        getServerResponse(ServerResponseCode().JOB_ALREADY_IN_RUNNING_STATE, false)
                    }
                }else{
                    getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST, false)
                }
    }

    @RequestMapping("resume", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun resume(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.resume()")
        val jobName = requestBody["jobName"]?:""
        return if(jobService.isJobWithNamePresent(jobName)){
                    if (jobService.getJobState(jobName) == "PAUSE") {
                        log.info("Job current state is PAUSED, Resuming job...")
                        if(jobService.resumeJob(jobName)){
                            getServerResponse(ServerResponseCode().SUCCESS,true)
                        }else{
                            getServerResponse(ServerResponseCode().ERROR, false)
                        }
                    }else{
                        getServerResponse(ServerResponseCode().JOB_NOT_IN_PAUSED_STATE,false)
                    }
                }else{
                    getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST,false)
                }
    }

    @RequestMapping("update", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateJob(@RequestBody requestBody: MutableMap<String, String>): String {
        log.debug("JobController.updateJob()")

        val jobName = requestBody["jobName"]?:""
        val scheduleTime: Date = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(requestBody["scheduleTime"])
        val cronExpression = requestBody["cronExpression"]?:""
        val topicName = requestBody["topicName"]?:""

        if(jobName.trim() == ""){
            return getServerResponse(ServerResponseCode().JOB_NAME_NOT_PRESENT, false)
        }else if(topicName.trim() == ""){
            return getServerResponse(ServerResponseCode().TOPIC_NAME_NOT_PRESENT, false)
        }

        return if (jobService.isJobWithNamePresent(jobName)){
            if(when(cronExpression.trim() == ""){
                        true->jobService.updateOneTimeJob(jobName, SimpleJob().javaClass, scheduleTime, topicName)
                        else -> jobService.updateCronJob(jobName, CronJob().javaClass, scheduleTime, cronExpression, topicName)}){
                getServerResponse(ServerResponseCode().SUCCESS, jobService.getAllJobs())
            }else{
                getServerResponse(ServerResponseCode().ERROR, false)
            }
        }else{
            getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST, false)
        }

    }

    @RequestMapping("jobs", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllJobs(): String{
        val list: List<MutableMap<String, Any?>> = jobService.getAllJobs()
        log.info("JobController.getAllJobs() size: {}", list.size)
        return getServerResponse(ServerResponseCode().SUCCESS, list)
    }

    @RequestMapping("checkJobName", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkJobName(@RequestBody requestBody: MutableMap<String, String>): String{
        log.info("JobController.checkJobName()")
        val jobName = requestBody["jobName"]?:""
        return if(jobName.trim() == ""){
            getServerResponse(ServerResponseCode().JOB_NAME_NOT_PRESENT, false)
        }else {
            getServerResponse(ServerResponseCode().SUCCESS, jobService.isJobWithNamePresent(jobName))
        }
    }

    @RequestMapping("isJobRunning", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun isJobRunning(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.isJobRunning()")
        val jobName = requestBody["jobName"]?:""
        return getServerResponse(ServerResponseCode().SUCCESS, jobService.isJobRunning(jobName))
    }

    @RequestMapping("jobState", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getJobState(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.getJobState()")
        val jobName = requestBody["jobName"]?:""
        return if(jobService.isJobWithNamePresent(jobName)){
            getServerResponse(ServerResponseCode().SUCCESS, jobService.getJobState(jobName))
        }else{
            getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST, false)
        }

    }

    @RequestMapping("stop", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun stopJob(@RequestBody requestBody: MutableMap<String, String>): String{
        log.debug("JobController.stopJob()")
        val jobName = requestBody["jobName"]?:""
        return if(jobService.isJobWithNamePresent(jobName)){
            if(jobService.isJobRunning(jobName)){
                if(jobService.stopJob(jobName)){
                    getServerResponse(ServerResponseCode().SUCCESS, true)
                }else{
                    getServerResponse(ServerResponseCode().ERROR, false)
                }
            }else{
                getServerResponse(ServerResponseCode().JOB_NOT_IN_RUNNING_STATE, false)
            }
        }else{
            getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST, false)
        }
    }

    @RequestMapping("start", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startJobNow(@RequestBody requestBody: MutableMap<String, String>):String{
        log.debug("JobController.startJobNow()")
        val jobName = requestBody["jobName"]?:""
        return if(jobService.isJobWithNamePresent(jobName)){
            if(!jobService.isJobRunning(jobName)){
                if(jobService.startJobNow(jobName)){
                    getServerResponse(ServerResponseCode().SUCCESS, true)
                }else{
                    getServerResponse(ServerResponseCode().ERROR, false)
                }
            }else{
                getServerResponse(ServerResponseCode().JOB_ALREADY_IN_RUNNING_STATE, false)
            }
        }else{
            getServerResponse(ServerResponseCode().JOB_DOESNT_EXIST, false)
        }

    }

    @RequestMapping("run", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun runJob(@RequestBody requestBody: MutableMap<String, String>): String{
        log.info("JobController.runJob")
        val topic = requestBody["topic"]?:""
        val message = requestBody["message"]?:""
        try{
            logger.info("request run topic {}" , topic)
            logger.info("request run topology {}" , message)
            producer.sendMessage(topic,null, message)
        }catch (e:Exception){
            logger.error(e.toString(), e)
        }
        return getServerResponse(ServerResponseCode().SUCCESS, true)
    }

    fun getServerResponse(responseCode: Int, data: Any): String
            = gson.toJson(mapOf("responseCode" to responseCode, "data" to data))
}