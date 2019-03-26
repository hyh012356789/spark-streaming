
#   Welcome to Spark Structured Streaming + Kafka  SQL Read / Write

Structured Streaming is built upon the Spark SQL engine, and improves upon the constructs from Spark SQL Data Frames and Datasets so you can write streaming queries in the same way you would write batch queries.

spark-sql-kafka supports to run SQL query over the topics read and write.


### Application setup for Development.

-  java 1.8
-   Maven 3 +
-   Recommended IDE (intellij/Scala IDE)
-   Git
-   Maven build should have a SNAPSHOT version for jar.


### Application setup for deployment.

-   java 1.8
-   Generate the json schema from online(https://easy-json-schema.github.io/) and place in config file.
-   Spark 2.2.0
-   Kafka 0.10
-   Scala 2.11
-   Dev/Uat build should have a SNAPSHOT version for jar, Prod should have release version of jar ( without SNAPSHOT)


##### Read from kafka

`val ksDf = spark
         .readStream
         .format("kafka")
         .options(kafkaConsumerMap)
         .load()`
##### Convert JSON string to DF with typed JSON schema

Once data has been projected then we can apply our SQL operation( filter condition, aggregation ...)

`val stDF=ksDf
         .selectExpr("CAST(value AS STRING)")
         .select(from_json($"value", json_schema) as "data")
         .select("data.*")`
         
##### Streaming write Df to - [ console/kafka/file/inMemory ]

We apply interactive SQL query to form DF

`val stdfk=stDF
             .writeStream
             .queryName("stream_tble")
             .outputMode("complete")
             .format("memory")
             .start()
           stdfk.awaitTermination()
           spark.sql("select deviceName,IMEI-number,device-location from stream_tble").show(false)`
      
