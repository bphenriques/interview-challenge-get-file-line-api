/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.manager

import com.bphenriques.helpers.{BaseSpec, MockRoundRobinLinesManager}

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

  it must "make available the line as soon as it was set" in {
    val rows = Table(
      ("Shard", "Line Number", "Line"),
      (0, 1, "Line 1"),
      (1, 2, "Line 2"),
      (0, 3, "Line 3"),
      (1, 4, "Line 4"),
      (0, 5, "Line 5"),
    )

    /**
      * FIXME:
      *
      * Flaky test due to a open issue https://github.com/scalatest/scalatest/issues/784. The workaround suggested
      * leads to compilation error.
      *
      * BH-4 | Bruno Henriques (bphenriques@outlook.com@gmail.com)
      */
    val cluster = new MockRoundRobinLinesManager(2)
    forAll (rows) { (_: Int, lineNumber: Int, line: String) =>
      whenReady(cluster.setString(lineNumber, line).flatMap(_ => cluster.getString(lineNumber))) { result =>
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

    forAll (rows) { (_: Int, lineNumber: Int, line: String) =>
      whenReady(cluster.setString(lineNumber, line)) { _ =>
        whenReady(cluster.count()) { result =>
          result shouldEqual lineNumber
        }
      }
    }
  }
}
