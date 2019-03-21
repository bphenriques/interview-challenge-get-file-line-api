package com.salsify.lineserver.client.manager.strategies

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.manager.strategies.RoundRobinShardManagerConfig
import com.salsify.lineserver.common.config.HostConfig

/**
  * Tests [[RoundRobinShardManagerConfig]].
  */
class RoundRobinShardsManagerConfigSpec extends BaseSpec {

  it must "reject empty set of shards" in {
    assertThrows[Exception] {
      RoundRobinShardManagerConfig(List())
    }
  }

  it must "accept non-empty set of shards" in {
    val shards = List(
      HostConfig("localhost", 8080),
      HostConfig("localhost", 8081),
    )

    val config = RoundRobinShardManagerConfig(shards)
    config.shards shouldEqual shards
  }
}
