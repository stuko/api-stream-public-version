package com.stuko.stream.api.module.impl

import com.stuko.stream.api.module.IModule
import org.apache.storm.spout.SpoutOutputCollector

interface ISpoutModule : IModule {
    fun execute(map : MutableMap<String,Any>?, collector: SpoutOutputCollector? ) : MutableMap<String,Any>?
}