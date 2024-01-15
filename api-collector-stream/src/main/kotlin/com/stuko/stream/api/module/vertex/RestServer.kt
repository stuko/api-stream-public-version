package com.stuko.stream.api.module.vertex

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stuko.stream.api.common.Log
import com.stuko.stream.api.module.impl.AbstractSpoutModule
import io.netty.buffer.ByteBuf
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.storm.spout.SpoutOutputCollector
import org.apache.storm.tuple.Values
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class RestServer {
  val gson = Gson()
  private var _collector:  SpoutOutputCollector? = null
  var producerTopic:String? = ""
  var logger: Logger = LoggerFactory.getLogger(RestServer::class.java)
  var kafkaProps: Properties? = null
  var parentSpout: AbstractSpoutModule? = null

  fun start(spout: AbstractSpoutModule, port:Int, topic:String?, props: Properties?){
    this.parentSpout = spout
    this.producerTopic = topic
    this.kafkaProps = props
    Vertx.vertx()
      .createHttpServer()
      .requestHandler { req: HttpServerRequest ->
        req.bodyHandler { bodyHandler ->
          try {
            logger.info("Body is {}", bodyHandler.toString())
            val body: JsonObject = bodyHandler.toJsonObject()
            logger.info("Body to JSON is {}", body.toString())
            next(body.toString())
          }catch(ex : Exception){
            logger.error(ex.toString())
          }
        }
        req.response().end("Hello World!")
      }
      .listen(port
      ) { handler: AsyncResult<HttpServer?> ->
        if (handler.succeeded()) {
          logger.info("http://localhost:{}/",port)
        } else {
          logger.error("Failed to listen on port {}", port)
        }
      }
  }

  fun next(json:String): Unit {
    try {
      logger.info("############ RestServer ###############")
      logger.info("RestServer called...... {} " , this.producerTopic)
      logger.info("############ RestServer ###############")
      if(this.producerTopic != null && "" != this.producerTopic) {
        var producer: KafkaProducer<String, String> = KafkaProducer<String, String>(kafkaProps)
        logger.info("RestServer prepare for kafka : {} " , json)
        producer.send(ProducerRecord(this.producerTopic, json))
        logger.info("RestServer send to Kafka ...... {} " , json)
      }else{
        logger.info("RestServer can not send to Kakfa")
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        if(this.parentSpout != null) {
          this.parentSpout?.execute(gson.fromJson(json, mapType),this._collector)?.let {
            this.parentSpout?.emitMap(it)
          }
        }
      }

    }catch(e : Exception){
      logger.error(e.toString(),e)
    }
  }

}
