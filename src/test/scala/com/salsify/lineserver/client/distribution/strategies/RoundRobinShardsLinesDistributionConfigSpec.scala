package com.salsify.lineserver.client.distribution.strategies

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.common.config.HostConfig

/**
  * Tests [[RoundRobinShardsLinesDistributionConfig]].
  */
class RoundRobinShardsLinesDistributionConfigSpec extends BaseSpec {

  it must "reject empty set of shards" in {
    assertThrows[Exception] {
      RoundRobinShardsLinesDistributionConfig(List())
    }
  }

  it must "accept non-empty set of shards" in {
    val shards = List(
      HostConfig("localhost", 8080),
      HostConfig("localhost", 8081),
    )

    val config = RoundRobinShardsLinesDistributionConfig(shards)
    config.shards shouldEqual shards
  }
}
