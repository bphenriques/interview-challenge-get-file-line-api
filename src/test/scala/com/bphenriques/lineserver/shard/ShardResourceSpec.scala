/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.shard

import com.bphenriques.lineserver.shard.exception.KeyNotFoundException
import com.bphenriques.helpers.BaseSpec

/**
  * Tests for [[ShardResource]].
  */
class ShardResourceSpec extends BaseSpec {

  it must "reject keys if they were not inserted before" in {
    val rows = Table("Key", -1, 2, 6)

    val shardResource = new ShardResource()
    forAll (rows) { key: Int =>
      whenReady(shardResource.getString(key).failed) { e =>
        e shouldBe a[KeyNotFoundException]
        e.asInstanceOf[KeyNotFoundException].key shouldEqual key
      }
    }
  }

  it must "successfully put Key-Value pairs" in {
    val rows = Table(
      ("Value", "Key"),
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    val shardResource = new ShardResource()
    forAll (rows) { (value: String, key: Int) =>
      whenReady(shardResource.setString(key, value).flatMap(_ => shardResource.getString(key))) { result =>
        result shouldEqual value
      }
    }
  }

  it must "successfully keep track of the count" in {
    val rows = Table(
      ("Value", "Key"),
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    val shardResource = new ShardResource()
    forAll (rows) { (value: String, key: Int) =>
      whenReady(shardResource.setString(key, value).flatMap(_ => shardResource.count())) { result =>
        result shouldEqual key
      }
    }
  }
}
