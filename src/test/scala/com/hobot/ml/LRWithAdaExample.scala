package com.hobot.ml

import com.hobot.ml.lr.LogisticRegressionWithAda
import com.hobot.ml.optimization.{AdagradUpdater, AdamUpdater}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression._
import org.apache.spark.mllib.util.MLUtils



object LRWithAdaExample extends App {

  override def main(args: Array[String]): Unit = {

    val sc = new SparkContext(new SparkConf().setAppName("TESTADAOPTIMIZER").setMaster("local[2]"))
    val training = MLUtils.loadLibSVMFile(sc, args(0)).repartition(args(2).toInt)
    val testing = MLUtils.loadLibSVMFile(sc, args(1))
    val lr = new LogisticRegressionWithAda().setIntercept(false)
    Array(new AdagradUpdater, new AdamUpdater).foreach{ updater =>
      lr.optimizer
        .setRegParam(0.0)
        .setNumIterations(200)
        .setConvergenceTol(0.0005)
        .setUpdater(updater)
        .setStepSize(0.01)

      val currentTime = System.currentTimeMillis()
      val model = lr.run(training)
      val elapsedTime = System.currentTimeMillis() - currentTime
      // Compute raw scores on the test set.
      val predictionAndLabels = testing.map { case LabeledPoint(label, features) =>
        val prediction = model.predict(features)
        (prediction, label)
      }
      // Get evaluation metrics.
      val metrics = new MulticlassMetrics(predictionAndLabels)
      val accuracy = metrics.accuracy
      println(s"Accuracy = $accuracy, time elapsed: $elapsedTime millisecond.")
    }
    training.unpersist()
    sc.stop()
  }
}
