package com.stuko.stream.logger.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stuko.stream.logger.inf.Writer;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MongoDBWriterImpl implements Writer {

    static Logger logger = LoggerFactory.getLogger(MongoDBWriterImpl.class);

    @Value("${api.collector.mongo.url}")
    String url;

    @Value("${api.collector.mongo.min}")
    int min;

    @Value("${api.collector.mongo.max}")
    int max;

    @Value("${api.collector.mongo.db}")
    String db;

    @Value("${api.collector.mongo.log.collection}")
    String collection;


    int MAX_TRY_COUNT = 10;
    MongoClient mongoClient;

    @PostConstruct
    public void PostInit() throws Exception {
        int tryCount = 0;
        logger.debug("Mongodb client's from : database({})", db);
        logger.debug("Mongodb url {}", url);
        logger.debug("Mongodb new url {}", "mongodb://" + url);

        logger.info("#### Try first connection of MongoDB ,  MaxTryCount is {}",MAX_TRY_COUNT);
        while (this.mongoClient == null) {
            tryCount = connectAndGetTryCount(tryCount);
        }
        logger.info("#### We can obtain the connection of MongoDB");
    }

    private int connectAndGetTryCount(int tryCount) throws Exception {
        try {
            tryCount++;
            logger.info("#### try {} count is {}", url, tryCount);
            this.mongoClient = tryConnectToMongo();
            if(this.mongoClient != null) logger.info("Success of try {} connection ", url);
            else  logger.info("#### Fail of try {} connection ", url);
        } catch (Exception e1) {
            try{Thread.sleep(1000);}catch(Exception ee){}
            logger.error(e1.toString());
            if (tryCount > MAX_TRY_COUNT) {
                logger.info("#### try {} count is greater than {}",url,MAX_TRY_COUNT);
                throw new Exception("Max try count is over");
            }else{
                logger.info("#### try {} count is less than {}", url, MAX_TRY_COUNT);
            }
        } return tryCount;
    }

    private MongoClient tryConnectToMongo()  {
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        options.connectionsPerHost(max);
        options.minConnectionsPerHost(min);
        MongoClientURI uri = new MongoClientURI("mongodb://"+url, options);
        // MongoCredential credential = MongoCredential.createCredential("User", "DatabaseName", "password".toCharArray());
        return new MongoClient(uri);
    }

    private MongoDatabase getDataBase(String db) throws Exception {
        int tryCount = 0;
        MongoDatabase database = null;
        logger.debug("Mongodb client's from : database({})", db);
        logger.debug("Mongodb url {}", url);
        logger.debug("Mongodb new url {}", "mongodb://" + url);
        while(database == null){
            try {
                tryCount++;
                logger.debug("#### try(getDatabase) count is ",tryCount);
                database = this.mongoClient.getDatabase(db);
                if(database != null) logger.debug("Success of try(getDatabase) connection ");
                else  logger.debug("#### Fail of try(getDatabase) connection ");
            } catch (Exception e1) {
                try{Thread.sleep(1000);}catch(Exception ee){}
                logger.error(e1.toString());
                if (tryCount > MAX_TRY_COUNT) {
                    logger.info("#### try {} count is greater than {}",url, MAX_TRY_COUNT);
                    throw new Exception("Max try count is over");
                }else{
                    logger.info("#### try {} count is less than {}",url, MAX_TRY_COUNT);
                }
            }
        }
        return database;
    }

    private MongoCollection<Document> getCollection(String db , String collection ) throws Exception {
        MongoCollection<Document> col = this.getDataBase(db).getCollection(collection);
        if(col == null){
            this.getDataBase(db).createCollection(collection);
            return this.getDataBase(db).getCollection(collection);
        }else return col;
    }

    private void insert(String db , String collection , Map<String,Object> m) throws Exception {
        Document doc = getDocument(m);
        this.getCollection(db,collection).insertOne(doc);
    }

    private void delete(String db , String collection, Map<String,Object> m) throws Exception {
        Document doc = getDocument(m);
        this.getCollection(db,collection).deleteMany(doc);
    }

    private void update(String db , String collection , Map<String,Object> search, Map<String,Object> update) throws Exception {
        Document searchDoc = getDocument(search);
        Document updateDoc = getDocument(update);
        this.getCollection(db,collection).updateMany(searchDoc,updateDoc);
    }

    private List<Map<String,Object>> find(String db , String collection)  throws Exception{
        FindIterable<Document> resultDoc = this.getCollection(db,collection).find();
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (Document document : resultDoc) {
            mapList.add(document);
        }
        return mapList;
    }

    private List<Map<String,Object>> find(String db , String collection, Map<String,Object> search)  throws Exception{
        Document searchDoc = getDocument(search);
        FindIterable<Document> resultDoc = this.getCollection(db,collection).find(searchDoc);
        List<Map<String,Object>> mapList = new ArrayList<>();
        for(Document document : resultDoc) {
            mapList.add(document);
        }
        return mapList;
    }

    private Map<String,Object> findOne(String db , String collection,Map<String,Object> search)  throws Exception{
        Document searchDoc = getDocument(search);
        FindIterable<Document> resultDoc = this.getCollection(db,collection).find(searchDoc);
        Document oneDoc = resultDoc.first();
        Map<String,Object> map = new HashMap<>();
        if(oneDoc != null) {
            oneDoc.forEach((k, v) -> {
                map.put(k, v);
            });
        }
        return map;
    }


    private Document getDocument(Map<String,Object> m) {
        Document doc = new Document();
        m.forEach ((k, v) ->{
                doc.append(k, v);
        });
        return doc;
    }


    @Override
    public void log(Object object) {
        try {
            String json = (String)object;
            logger.info(json);
            Map<String,Object> map = new ObjectMapper().readValue(json,Map.class);
            this.insert(db,collection, map);
        } catch (Exception e) {
            logger.error(e.toString(),e);
        }
    }
}
