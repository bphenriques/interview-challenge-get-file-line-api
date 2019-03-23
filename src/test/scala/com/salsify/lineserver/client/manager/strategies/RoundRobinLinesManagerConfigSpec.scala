package com.salsify.lineserver.client.manager.strategies

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.manager.strategies.RoundRobinLinesManagerConfig
import com.salsify.lineserver.common.config.HostConfig

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
      HostConfig("localhost", 8080),
      HostConfig("localhost", 8081),
    )

    val config = RoundRobinLinesManagerConfig(shards)
    config.shards shouldEqual shards
  }
}
