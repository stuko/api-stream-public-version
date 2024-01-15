package com.stuko.data.api.collector.apicollectorscheduler.util

open class ServerResponseCode {

    //SPECIFIC ERROR CODES
    val JOB_WITH_SAME_NAME_EXIST:Int = 501
    val JOB_NAME_NOT_PRESENT:Int = 502
    val TOPIC_NAME_NOT_PRESENT:Int = 503

    val JOB_ALREADY_IN_RUNNING_STATE:Int = 510

    val JOB_NOT_IN_PAUSED_STATE:Int = 520
    val JOB_NOT_IN_RUNNING_STATE:Int = 521

    val JOB_DOESNT_EXIST:Int = 500

    //GENERIC ERROR
    val ERROR:Int = 600

    //SUCCESS CODES
    val SUCCESS:Int = 200
}