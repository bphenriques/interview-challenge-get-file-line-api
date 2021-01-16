/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.client.manager

import com.bphenriques.helpers.BaseSpec

/**
  * Tests [[ShardHttpClient]].
  */
class ShardHttpClientSpec extends BaseSpec {

  it must "make available the value as soon as it was set" in {
    val rows = Table(
      ("Key", "Value"),
      (0, "Value 0"),
      (1, "Value 1"),
      (2, "Value 2"),
      (3, "Value 3"),
      (4, "Value 4"),
      (5, "Value 5"),
    )

    /**
      * FIXME:
      *
      * Flaky test due to a open issue https://github.com/scalatest/scalatest/issues/784. The workaround suggested
      * leads to compilation error.
      *
      * BH-4 | Bruno Henriques (bphenriques@outlook.com@gmail.com)
      */
    forAll (rows) { (lineNumber: Int, value: String) =>
      val targetShard = newMockShardClient("test", 8080)
      whenReady(targetShard.setString(lineNumber, value) flatMap (_ => targetShard.getString(lineNumber))) { result =>
        result shouldEqual value
      }
    }
  }
}
