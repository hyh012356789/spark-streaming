/*
# Program      : Streaming the data.scala
# Date Created : 07/03/2018
# Description  : This is a Main class for Streaming Job
# Parameters   :
#
# Modification history:
#
# Date         Author               Description
# ===========  ===================  ============================================
# 09/03/2018   Anand Ayyasamy        Creation
# ===========  ===================  ============================================
*/
package com.org.data.struct.stream

import java.io.FileInputStream
import java.util.Properties

import com.org.data.struct.stream.utils._
import org.apache.log4j.Logger
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.zalando.spark.jsonschema.SchemaConverter

import scala.collection.JavaConverters._




object Structured_Streaming {

  private val LOGGER = Logger.getLogger(this.getClass.getName)
  private val prop = new Properties();
  prop.load(new FileInputStream("conf/structured-streaming.properties")) // Read config from property file
  private val propsMap=prop.asScala

  def main(args: Array[String]): Unit = {

    val kafkaConsumerMap=KafkaConfigUtils.getConsumerMap(propsMap)

    val schemaContent=propsMap(AppConstants.JSON_SCHEMA)
    val json_schema = SchemaConverter.convertContent(schemaContent)

    val streamSQL=propsMap(AppConstants.STREAM_SQL)
    //val checkpt_loc=propsMap(AppConstants.CHECKPOINT_LOC)

    /*enable in memory*/
    val inmem=propsMap("streaming.inmemory.sink").toBoolean
    /*enable status write*/
    val isWrite=propsMap("streaming.file.sink").toBoolean
    val hdfsDir=propsMap(AppConstants.HDFS_PATH)

    val spark = SparkSession
      .builder()
      //.master("local[2]")
      .appName("Structured_Streaming")
//      .enableHiveSupport()
      .getOrCreate();

    import spark.implicits._
    //spark.conf.set("spark.sql.streaming.checkpointLocation",checkpt_loc)

   // try {

      LOGGER.info("Starting consuming the data")

      // Read from kafka

      val ksDf = spark
        .readStream
        .format("kafka")
        .options(kafkaConsumerMap)
        .load()

      //ksDf.show(10)
      // convert JSON string to DF with typed JSON schema
      val stDF=ksDf
        .selectExpr("CAST(value AS STRING)")
        .select(from_json($"value", json_schema) as "data")
        .select("data.*")
        .selectExpr("CAST(eventTime AS timestamp) as eventTamp","*")//.createOrReplaceTempView("business_event")
    //spark.sql(streamSQL).show(10)
    // 计数

    val windowedCounts = stDF.groupBy(
      window($"eventTamp","1 hours","1 hours"), $"age",$"gender"
    )//.createOrReplaceTempView("business_event")
    .count()//.orderBy("window")
/*    val windowedCounts1 = stDF.groupBy(
      window($"eventTamp","1 hours","1 hours"), $"age",$"gender"
    )//.createOrReplaceTempView("business_event")
      .count()//.orderBy("window")*/

    val query = windowedCounts
      .writeStream
      .queryName("stream_tble")
      .outputMode("update")
      .format("console")
      .start()

/*    val query1 = windowedCounts1
      .writeStream
      .queryName("stream_tble1")
      .outputMode("update")
      .format("console")
      .start()*/

    spark.streams.awaitAnyTermination()
      // Streaming in memory query
/*        if(inmem){
          val stdfk=ksDf
            .writeStream
           // .queryName("stream_tble")
            .outputMode("complete")
            .format("console")
            .start()
          stdfk.awaitTermination()
          //spark.sql(streamSQL).show(true)
        }*/

      /*Streaming write Df to - file]*/

//      if(isWrite) {
//        val stdfk = stDF
//          .writeStream
//          .queryName("stream_tble")
//          .outputMode("complete")
//          .format("memory")
//          .start()
//        stdfk.awaitTermination()
//        val rs=spark.sql(streamSQL)
//
//        if (rs.count() > 0){
//          rs.write.mode(SaveMode.Append).orc(hdfsDir)
//        }
//
//      }

/*    }

    catch {
      case exception: Exception => {
        LOGGER.info(exception.getMessage)
        sys.exit(1)
      }
    }
    finally {

      //spark.stop()
      LOGGER.info("Closing the SparkSession")
      sys.exit(0)
    }*/


  }

}
