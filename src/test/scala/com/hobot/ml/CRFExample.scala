package com.hobot.ml

import java.io.{DataInputStream, DataOutputStream, FileInputStream, FileOutputStream}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import com.hobot.ml.crf.nlp._
import com.hobot.ml.crf.nlp.{CRF, Sequence}

object CRFExample {

  def main(args: Array[String]) {
/*    if (args.length != 3) {
      println("CRFExample <templateFile> <trainFile> <testFile>")
    }*/

    val templateFile = "D:\\2 work-space\\open-code\\spark-structured-streaming-kafka-sql-master\\data\\crf\\template"
    val trainFile = "D:\\2 work-space\\open-code\\spark-structured-streaming-kafka-sql-master\\data\\crf\\serialized\\train.data"
    val testFile = "D:\\2 work-space\\open-code\\spark-structured-streaming-kafka-sql-master\\data\\crf\\serialized\\test.data"

    val conf = new SparkConf().setAppName("CRFExample").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val templates: Array[String] = scala.io.Source.fromFile(templateFile).getLines().filter(_.nonEmpty).toArray
    val trainRDD: RDD[Sequence] = sc.textFile(trainFile).filter(_.nonEmpty).map(Sequence.deSerializer)
    //trainRDD.foreach(print(_))

    val model: CRFModel = CRF.train(templates, trainRDD, 0.25, 1, 100, 1E-3, "L1")

    val testRDD: RDD[Sequence] = sc.textFile(testFile).filter(_.nonEmpty).map(Sequence.deSerializer)
    //testRDD.foreach(println(_))

    /**
      * an example of model saving and loading
      */
/*    new java.io.File("target/model").mkdir()
    //model save as String
    new java.io.PrintWriter("target/model/model1") { write(CRFModel.save(model)); close() }
    val modelFromFile1 = CRFModel.load(scala.io.Source.fromFile("target/model/model1").getLines().toArray.head)
    // model save as RDD
    sc.parallelize(CRFModel.saveArray(model)).saveAsTextFile("target/model/model2")
    val modelFromFile2 = CRFModel.loadArray(sc.textFile("target/model/model2").collect())
    // model save as BinaryFile
    val path = "target/model/model3"
    new java.io.File(path).mkdir()
    CRFModel.saveBinaryFile(model, path)
    val modelFromFile3 = CRFModel.loadBinaryFile(path)*/

    /**
      * still use the model in memory to predict
      */
    val results: RDD[Sequence] = model.setNBest(10)
      .setVerboseMode(VerboseLevel1)
      .predict(testRDD)

    results.foreach( x=> println(x))

    val score = results
      .zipWithIndex()
      .map(_.swap)
      .join(testRDD.zipWithIndex().map(_.swap))
      .map(_._2)
      .map(x => x._1.compare(x._2))
      .reduce(_ + _)
    val total = testRDD.map(_.toArray.length).reduce(_ + _)
    println(s"Prediction Accuracy: $score / $total")

    sc.stop()
  }
}
