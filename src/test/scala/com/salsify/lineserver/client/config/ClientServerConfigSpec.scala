package com.salsify.lineserver.client.config

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.distribution.LinesDistribution
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.config.HostConfig

class ClientServerConfigSpec extends BaseSpec {

  /*
  it must "reject invalid arguments" in {
    val rows = Table(
      ("Host Config",       "Lines Supplier",      "Lines Distribution"),
      (LocalhostConfig,     SampleLinesProvider,   SampleLinesDistribution),                         // Empty List.
    )

    forAll (rows) { (hostConfig: HostConfig, lineSupplier: LinesInputSupplier, linesDistribution: LinesDistribution) =>
      assertThrows[Exception] {
        ClientServerConfig(hostConfig, lineSupplier, linesDistribution)
      }
    }
  }



  it must "accept valid configurations" in {
    val rows = Table(
      ("Host Config",   "Lines Supplier",         "Shards"),
      (LocalhostConfig, SampleLinesProviderConfig, List(HostConfig("test", 8080))),
      (LocalhostConfig, SampleLinesProviderConfig, List(HostConfig("test", 8080), HostConfig("test2", 8080)))
    )

    forAll (rows) { (hostConfig: HostConfig, lineSupplierConfig: LinesInputSupplier, shardsHosts: List[HostConfig]) =>
      val config = ClientServerConfig(hostConfig, lineSupplierConfig, shardsHosts)

      config.httpConfig shouldEqual hostConfig
      config.lineSupplierConfig shouldEqual lineSupplierConfig
      config.shardsHosts shouldEqual shardsHosts
    }
  }
  */
}
