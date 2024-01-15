package com.stuko.data.api.collector.dao

import com.stuko.data.api.collector.common.Log
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions.Builder
import com.mongodb.MongoClientURI
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


// Mongodb url changed....
@Component
class CommonMongoDAO {

    companion object : Log
    @Value("\${api.collector.mongo.url}")
    var url:String = "mongo1:27017,mongo2:27017,mongo3:27017"
    @Value("\${api.collector.mongo.min}")
    var min_count = 20
    @Value("\${api.collector.mongo.max}")
    var max_count = 50

    var mongoClient:MongoClient? = null

    @PostConstruct
    fun postConstruct() {
        var options:Builder = Builder()
        options.connectionsPerHost(max_count)
        options.minConnectionsPerHost(min_count)
        var uri: MongoClientURI = MongoClientURI("mongodb://$url",options);
        // if need below.....
        // var credential: MongoCredential = MongoCredential.createCredential("User", "DatabaseName", "password".toCharArray())
        this.mongoClient = MongoClient(uri)
    }

    fun getDataBase(db:String) : MongoDatabase?{
        var database: MongoDatabase?
        logger.info("Mongodb client's from : database({})", db)
        logger.info(">>>>>>>>>>>>>>>>>>> Mongodb url {}", url)
        logger.info("Mongodb new url {}", "mongodb://$url")
        database = this.mongoClient?.getDatabase(db)
        return database
    }

    fun getCollection(db : String, collection : String) :MongoCollection<Document>? {
        return this.getDataBase(db)?.getCollection(collection)
    }

    fun getCollections(db : String, collection : String) :MutableList<MutableMap<String,Any>>? {
        return this.getDataBase(db)?.getCollection(collection)?.find()?.toMutableList()
    }

    fun getCollections(db:String) : MutableList<MutableMap<String,Any>> ?{
        var mapList:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()
        for (doc in this.getDataBase(db)?.listCollections()!!) {
            mapList.add(doc.toMutableMap())
        }
        return mapList
    }

    fun insert(db:String, collection:String, m:Map<String,Any>):Unit{
        var doc: Document = getDocument(m)
        this.getCollection(db,collection)?.insertOne(doc)
    }

    fun delete(db:String, collection:String, m:Map<String,Any>):Unit{
        var doc: Document = getDocument(m)
        this.getCollection(db,collection)?.deleteMany(doc)
    }

    fun update(db:String, collection:String, search:Map<String,Any>, update:Map<String,Any>) : Unit{
        var searchDoc: Document = getDocument(search)
        var updateDoc: Document = getDocument(update)
        this.getCollection(db,collection)?.updateMany(searchDoc,Document("\$set",updateDoc))
    }

    fun find(db:String, collection:String, search:Map<String,Any> ) : MutableList<MutableMap<String,Any>>{
        var searchDoc: Document = getDocument(search)
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find(searchDoc)
        var mapList:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()
        resultDoc?.forEach { doc ->
            mapList.add(doc.toMutableMap())
        }
        return mapList
    }

    fun findOne(db:String, collection:String, search:Map<String,Any> ) : MutableMap<String,Any>{
        var searchDoc: Document = getDocument(search)
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find(searchDoc)
        var oneDoc: Document? = resultDoc?.first()
        var map:MutableMap<String,Any> = mutableMapOf<String,Any>()
        oneDoc?.forEach {k,v->
           map.put(k,v)
        }
        return map
    }

    fun findAll(db:String, collection:String) : MutableList<MutableMap<String,Any>>{
        var searchDoc: Document = getDocument(null)
        var resultDoc:FindIterable<Document>? = this.getCollection(db,collection)?.find()
        var mapList:MutableList<MutableMap<String,Any>> = mutableListOf<MutableMap<String,Any>>()
        resultDoc?.forEach { doc ->
          mapList.add(doc.toMutableMap())
        }
        return mapList
    }

    private fun getDocument(m: Map<String, Any>?): Document {
        val doc: Document = Document()
        m?.forEach { k, v ->
            doc.append(k, v)
        }
        return doc
    }

}

