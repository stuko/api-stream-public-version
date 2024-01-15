package com.stuko.stream.api.module.impl

import com.stuko.stream.api.module.IModule
import org.apache.storm.task.OutputCollector
import org.apache.storm.topology.BasicOutputCollector

interface IBoltModule : IModule {
    fun execute(map : MutableMap<String,Any>?, collector: OutputCollector? ) : MutableMap<String,Any>?
}