/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client.manager.strategies

import com.salsify.helpers.{BaseSpec, MockRoundRobinLinesManager}

/**
  * Tests [[RoundRobinLinesManager]].
  */
class RoundRobinLinesManagerSpec extends BaseSpec {

  it must "initialize correctly" in {
    val cluster = new MockRoundRobinLinesManager(2)
    cluster.shards.size shouldEqual 2
  }

  it must "assertion fails if line number is negative or zero" in {
    val cluster = new MockRoundRobinLinesManager(1)
    val rows = Table("Line Number", -1, 0)
    forAll (rows) { lineNumber: Int =>
      assertThrows[AssertionError] {
        cluster.getString(lineNumber)
      }
    }
  }

  it must "make available the line at the shard where it was set" in {
    val rows = Table(
      ("Shard", "Line Number", "Line"),
      (0, 1, "Line 1"),
      (1, 2, "Line 2"),
      (0, 3, "Line 3"),
      (1, 4, "Line 4"),
      (0, 5, "Line 5"),
    )

    val cluster = new MockRoundRobinLinesManager(2)
    forAll (rows) { (shard: Int, lineNumber: Int, line: String) =>
      val targetShard = cluster.shards(shard)
      whenReady(targetShard.setString(lineNumber, line) flatMap (_ => targetShard.getString(lineNumber))) { result =>
        result shouldEqual line
      }
    }
  }

  it must "return the correct count" in {
    val rows = Table(
      ("Shard", "Line Number", "Line"),
      (0, 1, "Line 1"),
      (1, 2, "Line 2"),
      (0, 3, "Line 3"),
      (1, 4, "Line 4"),
      (0, 5, "Line 5"),
    )

    val cluster = new MockRoundRobinLinesManager(2)
    whenReady(cluster.count()) { count =>
      count shouldEqual 0
    }

    forAll (rows) { (shard: Int, lineNumber: Int, line: String) =>
      val targetShard = cluster.shards(shard)
      whenReady(targetShard.setString(lineNumber, line) flatMap (_ => targetShard.getString(lineNumber))) { result =>
        result shouldEqual line
      }
    }
  }
}
