package com.hobot.graphx

import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.graphx.{Edge, Graph, GraphLoader, lib}
import org.apache.spark.sql.SparkSession

object GraphxTest {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder().appName(this.getClass.getName).master("local[*]").getOrCreate()
    val sc = sparkSession.sparkContext

    Logger.getRootLogger.setLevel(Level.ERROR)

    val g = GraphLoader.edgeListFile(sc, "D:\\2 work-space\\open-code\\spark-structured-streaming-kafka-sql-master\\data\\aa.txt")
    val label = LabelPropagation.run(g ,200 )
    //label.
    //println(g.vertices.collect().apply(0))
    //println(label.vertices.collect().apply(0))
    label.vertices.map(x=>x._1).distinct().foreach(println(_))
/*    val reuslt = g.personalizedPageRank(9207016, 0.001)
      .vertices
      .filter(_._1 != 9207016)
      .reduce((a, b) => if (a._2 < b._2) a else b)
    println(reuslt)*/

  }
}
