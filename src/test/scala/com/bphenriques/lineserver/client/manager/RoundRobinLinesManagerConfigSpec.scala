/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.manager

import com.bphenriques.helpers.BaseSpec

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
      ShardHttpClientConfig("http://localhost", 8080, 1),
      ShardHttpClientConfig("http://localhost", 8081, 1),
    )

    val config = RoundRobinLinesManagerConfig(shards)
    config.shards shouldEqual shards
  }
}
