/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.common.config

import com.bphenriques.helpers.BaseSpec

/**
  * Tests for [[ServerBindingConfig]].
  */
class ServerBindingConfigSpec extends BaseSpec {

  it must "reject invalid arguments" in {
    val rows = Table(
      ("Host", "Port"),
      ("", 8080),        // Empty host.
      (" ", 8080),       // Whitespace host.
      ("localhost", -1), // Outside of the range.
      ("localhost", 10), // Reserved.
    )

    forAll (rows) { (host: String, port: Int) =>
      assertThrows[Exception] {
        ServerBindingConfig(host, port)
      }
    }
  }

  it must "accept valid configurations" in {
    val rows = Table(
      ("Host", "Port"),
      ("0.0.0.0",   8080),
      ("localhost", 8080),
      ("127.0.0.1", 8081),
      ("shard1",    8090),
    )

    forAll (rows) { (host: String, port: Int) =>
      val config = ServerBindingConfig(host, port)

      config.host shouldEqual host
      config.port shouldEqual port
    }
  }
}
