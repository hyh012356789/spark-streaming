package com.org.data.struct.stream

import org.apache.spark.sql.SparkSession

object KafkaStreaming {
  def main(args: Array[String]) {
    // Subscribe to 1 topic
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("Structured_Streaming")
      //      .enableHiveSupport()
      .getOrCreate();

    val df = spark
      .readStream
      .format("kafka")

      .option("kafka.bootstrap.servers", "10.10.108.101:9092")

      .option("subscribe", "test123")

      .load()

    val aa = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
     // .as[(String, String)]

     val query = aa
    .writeStream
    .outputMode("append")
    .format("console")
    .start()

    spark.streams.awaitAnyTermination()

  }
}
