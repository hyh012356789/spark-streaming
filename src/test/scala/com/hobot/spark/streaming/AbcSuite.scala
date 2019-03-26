package com.hobot.spark.streaming

import org.scalatest.{FunSuite, ShouldMatchers}

class AbcSuite extends FunSuite with ShouldMatchers {
  test("abc") {
    val abc = "abc"
    abc should be ("abc")
  }
  test("bc") {
    val abc = "sadadsad"
    abc.length should be (8)
  }
}
