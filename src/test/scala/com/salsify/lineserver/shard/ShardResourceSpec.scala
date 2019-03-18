package com.salsify.lineserver.shard

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.shard.exception.KeyNotFoundException

class ShardResourceSpec extends BaseSpec {

  val ShardResource = new ShardResource()

  it must "reject keys if they were not inserted before" in {
    val rows = Table("Key", -1, 2, 6)
    forAll (rows) { key: Int =>
      whenReady(ShardResource.getInt(key).failed) { e =>
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

    forAll (rows) { (value: String, key: Int) =>
      whenReady(ShardResource.setInt(key, value).flatMap(_ => ShardResource.getInt(key))) { result =>
        result shouldEqual value
      }
    }
  }
}
