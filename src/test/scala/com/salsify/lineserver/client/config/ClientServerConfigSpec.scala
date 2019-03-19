package com.salsify.lineserver.client.config

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.ConfigFactory

class ClientServerConfigSpec extends BaseSpec {

  it must "parse a valid config" in {
    val validConfig =
      """
        |http {
        |    host = "0.0.0.0"
        |    port = 8080
        |  }
        |
        |  input {
        |    type = "local-file"
        |    local-file {
        |      path = "/tmp/file.txt"
        |    }
        |  }
        |
        |  distribution {
        |    type = "shards-round-robin"
        |    shards-round-robin {
        |      shards: [
        |        {
        |          host = "http://shard1"
        |          port = 8080
        |        },
        |        {
        |          host = "http://shard2"
        |          port = 8080
        |        }
        |      ]
        |    }
        |  }
      """

    val conf = ClientServerConfig.fromConfig(ConfigFactory.parseString(validConfig)).get
    conf.binding shouldEqual HostConfig("0.0.0.0", 8080)
  }
}
