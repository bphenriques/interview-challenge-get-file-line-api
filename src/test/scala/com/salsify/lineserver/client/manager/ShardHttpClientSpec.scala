package com.salsify.lineserver.client.manager

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.manager.ShardHttpClient

/**
  * Tests [[ShardHttpClient]].
  */
class ShardHttpClientSpec extends BaseSpec {

  it must "store the host and the port" in {
    val host = newMockShardClient("host", 8080)

    host.host shouldEqual "host"
    host.port shouldEqual 8080
  }

  it must "make available the value as soon as it was set" in {
    val rows = Table(
      ("Key", "Value"),
      (-1, "Value -1"),
      (0, "Value 0"),
      (1, "Value 1"),
      (2, "Value 2"),
      (3, "Value 3"),
      (4, "Value 4"),
      (5, "Value 5"),
    )

    forAll (rows) { (lineNumber: Int, value: String) =>
      val targetShard = newMockShardClient("test", 8080)
      whenReady(targetShard.setInt(lineNumber, value) flatMap (_ => targetShard.getInt(lineNumber))) { result =>
        result shouldEqual value
      }
    }
  }
}
