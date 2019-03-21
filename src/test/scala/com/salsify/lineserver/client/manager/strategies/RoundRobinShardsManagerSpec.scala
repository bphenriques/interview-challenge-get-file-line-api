package com.salsify.lineserver.client.manager.strategies

import com.salsify.helpers.{BaseSpec, MockRoundRobinShardsManager}
import com.salsify.lineserver.client.manager.strategies.RoundRobinShardsManager

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Tests [[RoundRobinShardsManager]].
  */
class RoundRobinShardsManagerSpec extends BaseSpec {

  it must "initialize correctly" in {
    val cluster = new MockRoundRobinShardsManager(2)
    cluster.shards.size shouldEqual 2
  }

  it must "assertion fails if line number is negative or zero" in {
    val cluster = new MockRoundRobinShardsManager(1)
    val rows = Table("Line Number", -1, 0)
    forAll (rows) { lineNumber: Int =>
      assertThrows[AssertionError] {
        cluster.getInt(lineNumber)
      }
    }
  }

  it must "return the same shard if provided a single shard" in {
    val cluster = new MockRoundRobinShardsManager(1)
    cluster.shardFor(1) shouldEqual cluster.shardFor(2)
  }

  it must "use round-robin to select the shard given a line number" in {
    val cluster = new MockRoundRobinShardsManager(2)

    cluster.shardFor(1) shouldEqual cluster.shards(1)
    cluster.shardFor(2) shouldEqual cluster.shards(0)
    cluster.shardFor(3) shouldEqual cluster.shards(1) // line number > number of shards
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

    val cluster = new MockRoundRobinShardsManager(2)
    forAll (rows) { (shard: Int, lineNumber: Int, line: String) =>
      val targetShard = cluster.shards(shard)
      whenReady(targetShard.setInt(lineNumber, line) flatMap (_ => targetShard.getInt(lineNumber))) { result =>
        result shouldEqual line
      }
    }
  }

  it must "setup correctly, i.e., distribute all the lines throughout the shards in the cluster" in {
    val rows = Table(
      ("Line", "Line Number"),
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    val cluster = new MockRoundRobinShardsManager(2)
    Await.result(cluster.setup(SampleLinesProvider), 5 second)
    forAll (rows) { (line: String, lineNumber: Int) =>
      whenReady(cluster.getInt(lineNumber)) { result =>
        result shouldEqual line
      }
    }
  }
}
