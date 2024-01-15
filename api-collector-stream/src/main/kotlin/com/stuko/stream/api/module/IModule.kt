package com.stuko.stream.api.module

interface IModule {

    enum class KIND {START,END,API,DBMS,SQL,FILE,FORK,FILTER,LINK,TCP,HTTP,WAIT,SELECTOR,MONGO,TRANSFORM,FUNCTION,RANGE}
    var moduleType : KIND
    var moduleKey : String
    var moduleCount: Int
    var topologyId: String
    var collection: String
    var moduleName: String
    var db: String

    fun getProperty():MutableList<MutableMap<String,Any>>
    fun setProperty(p:MutableList<MutableMap<String,Any>>) : Unit
    fun getProperty(name:String) : String
    fun isEnd() : Boolean
    fun setEnd(end:Boolean) : Unit
    fun init(): Unit
    fun start(): Boolean

}