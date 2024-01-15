package com.stuko.stream.api.dao.impl

import com.stuko.stream.api.common.Log
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientURI
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.stuko.stream.api.config.Singleton
import org.bson.Document
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

// Mongodb url changed....
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="api.collector.mongo")
class MongoDAOConfig{
    var url:String? = null

    @Bean(name = ["MongoDAO"])
    fun getMongoDAO(): MongoDAO {
        Singleton.putMongoDAO("MongoDAO",MongoDAO(url))
        return Singleton.getMongoDAO("MongoDAO")!!
    }
}


class MongoDAO(var url:String?){
    companion object : Log
    var min = 50
    var max = 100
    var mongoClient:MongoClient? = null
    val MAX_TRY_COUNT = 600
    @PostConstruct
    fun postConstruct() {
        var tryCount = 0
        logger.info("#### Try first connection of MongoDB ,  MaxTryCount is ${MAX_TRY_COUNT}")
        while (this.mongoClient == null) {
            try {
                tryCount++
                logger.info("#### try count is $tryCount")
                this.mongoClient = tryConnectToMongo()
                if(this.mongoClient != null) logger.info("Success of try connection ")
                else  logger.info("#### Fail of try connection ")
            } catch (e1: Exception) {
                Thread.sleep(1000)
                logger.error(e1.toString())
                if (tryCount > MAX_TRY_COUNT) {
                    logger.info("#### try count is greater than $MAX_TRY_COUNT")
                    throw Exception("$e1 - Max try count is over ")
                }else{
                    logger.info("#### try count is less than $MAX_TRY_COUNT")
                }
            }
        }
        logger.info("#### We can obtain the connection of MongoDB")
    }

    private fun tryConnectToMongo() :MongoClient?  {
        var options: MongoClientOptions.Builder = MongoClientOptions.Builder()
        options.connectionsPerHost(max)
        options.minConnectionsPerHost(min)
        var uri: MongoClientURI = MongoClientURI("mongodb://$url", options);
        // if need below.....
        // var credential: MongoCredential = MongoCredential.createCredential("User", "DatabaseName", "password".toCharArray())
        return MongoClient(uri)
    }

    fun getDataBase(db:String) : MongoDatabase?{
        var tryCount = 0
        var database: MongoDatabase? = null
        logger.info("Mongodb client's from : database({})", db)
        logger.info("Mongodb url {}", url)
        logger.info("Mongodb new url {}", "mongodb://$url")
        while(database == null){
            try {
                tryCount++
                logger.debug("#### try(getDatabase) count is $tryCount")
                database = this.mongoClient?.getDatabase(db)
                if(database != null) logger.debug("Success of try(getDatabase) connection ")
                else logger.debug("#### Fail of try(getDatabase) connection ")
            } catch (e1: Exception) {
                Thread.sleep(1000)
                logger.info("Mongodb client's from : database({})", db)
                logger.info("Mongodb url {}", url)
                logger.info("Mongodb new url {}", "mongodb://$url")
                logger.error(e1.toString(),e1)
                if (tryCount > MAX_TRY_COUNT) {
                    logger.info("#### try(getDatabase) count is greater than $MAX_TRY_COUNT")
                    throw Exception("$e1 - Max try(getDatabase) count is over ")
                }else{
                    logger.info("#### try(getDatabase) count is less than $MAX_TRY_COUNT")
                }
            }
        }
        return database
    }

    fun getCollection(db : String, collection : String) :MongoCollection<Document>? {
        val col:MongoCollection<Document>? = this.getDataBase(db)?.getCollection(collection)
        if(col == null){
            this.getDataBase(db)?.createCollection(collection)
            return this.getDataBase(db)?.getCollection(collection)
        }else return col
    }

    fun getCollections(db : String, collection : String) :MutableList<MutableMap<String,Any>>? {
        return this.getDataBase(db)?.getCollection(collection)?.find()?.toMutableList()
    }

    fun insert(db:String, collection:String, m:MutableMap<String,Any>):Unit{
        var doc: Document = getDocument(m)
        this.getCollection(db,collection)?.insertOne(doc)
    }

    fun delete(db:String, collection:String, m:MutableMap<String,Any>):Unit{
        var doc: Document = getDocument(m)
        this.getCollection(db,collection)?.deleteMany(doc)
    }

    fun update(db:String, collection:String, search:MutableMap<String,Any>, update:Map<String,Any>) : Unit{
        var searchDoc: Document = getDocument(search)
        var updateDoc: Document = getDocument(update)
        this.getCollection(db,collection)?.updateMany(searchDoc,updateDoc)
    }

    fun find(db:String, collection:String) : MutableList<MutableMap<String,Any>>{
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find()
        var mapList:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()
        resultDoc?.forEach { doc ->
            mapList.add(doc.toMutableMap())
        }
        return mapList
    }

    fun find(db:String, collection:String, search:MutableMap<String,Any> ) : MutableList<MutableMap<String,Any>>{
        logger.info("############ Search ############")
        logger.info("DB by {}" , db)
        logger.info("Collection by {}" , collection)
        logger.info("Search by {}" , search)
        logger.info("############ Search ############")
        var searchDoc: Document = getDocument(search)
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find(searchDoc)
        var mapList:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()
        resultDoc?.forEach { doc ->
            // mapList.add(mutableMapOf<String,Any>() )
            logger.info("############ Search Result ############")
            logger.info("search result = {}" , doc.toMutableMap())
            mapList.add(doc.toMutableMap())
            logger.info("############ Search Result ############")
            /*
            doc.forEach {k,v->
                var m = mapList.get(mapList.size -1)
                if( m is MutableMap<String,Any>){
                    m.put(k,v)
                }
            }
            */
        }
        logger.info("search END")
        return mapList
    }

    fun findOne(db:String, collection:String, search:MutableMap<String,Any> ) : MutableMap<String,Any>{
        var searchDoc: Document = getDocument(search)
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find(searchDoc)
        var oneDoc: Document? = resultDoc?.first()
        var map:MutableMap<String,Any> = mutableMapOf<String,Any>()
        oneDoc?.forEach {k,v->
           map.put(k,v)
        }
        return map
    }


    private fun getDocument(m: Map<String, Any>): Document {
        val doc: Document = Document()
        m.forEach { k, v ->
            doc.append(k, v)
        }
        return doc
    }

}
