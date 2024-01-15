package com.stuko.stream.api.config


import com.stuko.stream.api.common.Log
import com.stuko.stream.api.dao.impl.MongoDAO
import com.stuko.stream.api.server.IStreamServer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.Serializable
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.sql.Connection
import java.time.Duration
import javax.sql.DataSource


// @Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.db.datasource")
class StreamConfig{
    companion object : Log
    @Autowired
    lateinit var server : IStreamServer

    // @Value("\${spring.db.datasource.validationQuery}")
    var validationQuery : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }

    // @Value("\${spring.db.datasource.name}")
    var name : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.driverClassName}")
    var driverClassName : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.driverUrl}")
    var driverUrl : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.username}")
    var username : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.password}")
    var password : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.maximum}")
    var maximum : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }
    // @Value("\${spring.db.datasource.validationTimeOut}")
    var validationTimeOut : List<String> = mutableListOf()
        get() = field
        set(value) {
            field = value
        }

    @Bean
    fun stream(): IStreamServer{
        var topologyGroupName: String = System.getenv("TopologyGroupName")?: "TopologyGroup1"
        if(topologyGroupName.equals("")) topologyGroupName = System.getProperty("TopologyGroupName")
        logger.info("TopologyName is {}" , topologyGroupName);
        logger.info("######### Stream Server's DB List ###########")

        for(x in 0 until name.size){
            logger.info("---- Data Source Info ----")
            logger.info("driver class name [{}] : {} ", x, driverClassName[x])
            logger.info("driverUrl [{}] : {} ", x, driverUrl[x])
            logger.info("name [{}] : {} ", x, username[x])
            logger.info("password [{}] : {} ", x, password[x])
            logger.info("datasource name [{}] : {} ", x, name[x])
            logger.info("maximum [{}] : {} ", x, maximum[x])
            logger.info("validationQuery [{}] : {} ", x, validationQuery[x])
            logger.info("validationTimeOut [{}] : {} ", x, validationTimeOut[x])
            logger.info("---- Data Source Info ----")
            Singleton.putDataSource(name[x],Singleton.createAndSetDataSource(name[x],driverUrl[x],username[x],password[x],driverClassName[x],maximum[x],validationQuery[x],validationTimeOut[x]))
        }

        this.server.start(topologyGroupName)
        return this.server
    }
}

open class DataSourceSingleton{
    public var map:MutableMap<String, DataSource> = mutableMapOf()
    public var dataSourceObjectMap:MutableMap<String, DataSourceObject> = mutableMapOf()
    public var connectionMap:MutableMap<String, Connection> = mutableMapOf()
    public var mongoMap:MutableMap<String,MongoDAO> = mutableMapOf()
    fun initDataSource() : Unit {
    }
    fun getDataSource(name:String) : DataSource? {
        return this.map[name]
    }
    fun putDataSource(name:String, source:DataSource) : Unit {
        this.map[name] = source
    }
    fun getMongoDAO(name:String) : MongoDAO? {
        return this.mongoMap[name]
    }
    fun putMongoDAO(name:String, source:MongoDAO) : Unit {
        this.mongoMap[name] = source
    }
    fun getConnection(name:String) : Connection? {
        return this.connectionMap[name]
    }
    fun putConnection(name:String,connection:Connection) :Unit {
        this.connectionMap[name] = connection
    }

    fun reloadConnection():Unit{
        dataSourceObjectMap.forEach{
            putDataSource(it.key,createDataSource(it.value.url,
                             it.value.username,
                             it.value.password,
                             it.value.driverClassName,
                             it.value.maximum,
                             it.value.validationQuery,
                             it.value.validationTimeOut))
        }
    }

    fun reloadConnection(key:String):Unit{
        dataSourceObjectMap[key]?.let {
            putDataSource(key,
                        createDataSource(
                            it.url,
                            it.username,
                            it.password,
                            it.driverClassName,
                            it.maximum,
                            it.validationQuery,
                            it.validationTimeOut))
        }
    }

    fun createAndSetDataSource(name:String, driverUrl:String, username:String, password:String, driverClassName:String, maximum:String, validationQuery:String, validationTimeOut:String): DataSource{
        setDataSourceObject(name, driverUrl,username,password,driverClassName,maximum,validationQuery,validationTimeOut)
        return createDataSource(driverUrl,username,password,driverClassName,maximum,validationQuery,validationTimeOut)
    }

    fun setDataSourceObject(name:String, driverUrl:String, username:String, password:String, driverClassName:String, maximum:String, validationQuery:String, validationTimeOut:String): Unit{
        DataSourceObject(name, driverUrl,username,password,driverClassName,maximum,validationQuery,validationTimeOut).also { dataSourceObjectMap[name] = it }
    }
    fun createDataSource(driverUrl:String, username:String, password:String, driverClassName:String, maximum:String, validationQuery:String, validationTimeOut:String) :DataSource{
        /*
        val config = HikariConfig()
        config.setJdbcUrl( driverUrl[x] )
        config.setUsername( username[x] )
        config.setPassword( password[x] )
        config.setDriverClassName( driverClassName[x] )
        config.setMaximumPoolSize(maximum[x].toInt())
        config.addDataSourceProperty( "autoCommit" , "true" );
        config.addDataSourceProperty( "maximumPoolSize" , maximum[x] );
        config.addDataSourceProperty( "poolName" , name[x] );
        config.connectionTestQuery = validationQuery[x]
        config.validationTimeout = validationTimeOut[x].toLong()
        var ds : HikariDataSource = HikariDataSource( config )
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName(driverClassName[x])
        dataSourceBuilder.url(url[x])
        dataSourceBuilder.username(username[x])
        dataSourceBuilder.password(password[x])
        */
        val ds = BasicDataSource()
        ds.url = driverUrl
        ds.username = username
        ds.password = password
        ds.driverClassName = driverClassName
        ds.maxIdle = maximum.toInt()
        ds.maxTotal = maximum.toInt()
        ds.validationQuery = validationQuery
        ds.setValidationQueryTimeout(Duration.ofMillis(validationTimeOut.toLong()))
        ds.setDefaultQueryTimeout(Duration.ofMillis(validationTimeOut.toLong()))
        ds.setRemoveAbandonedTimeout(Duration.ofMillis(validationTimeOut.toLong()))
        return ds
    }

}

data class DataSourceObject(
    val name: String,
    val url: String,
    val username: String,
    val password: String,
    val driverClassName:String,
    val maximum: String,
    val validationQuery: String,
    val validationTimeOut: String
)

@Component
object Singleton : DataSourceSingleton(){
}
