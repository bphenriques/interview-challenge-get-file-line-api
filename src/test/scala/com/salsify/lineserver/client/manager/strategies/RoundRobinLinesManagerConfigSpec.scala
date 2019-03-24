package com.salsify.lineserver.client.manager.strategies

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.manager.ShardHttpClientConfig

/**
  * Tests [[RoundRobinLinesManagerConfig]].
  */
class RoundRobinLinesManagerConfigSpec extends BaseSpec {

  it must "reject empty set of shards" in {
    assertThrows[Exception] {
      RoundRobinLinesManagerConfig(List())
    }
  }

  it must "accept non-empty set of shards" in {
    val shards = List(
      ShardHttpClientConfig("localhost", 8080, 1),
      ShardHttpClientConfig("localhost", 8081, 1),
    )

    val config = RoundRobinLinesManagerConfig(shards)
    config.shards shouldEqual shards
  }
}
