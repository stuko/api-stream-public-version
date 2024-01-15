package com.stuko.stream.api.server.helper

interface Helper {
    fun help(collection: String, schema:String, param : MutableMap<String,Any> ) :MutableList<MutableMap<String,Any>>
}