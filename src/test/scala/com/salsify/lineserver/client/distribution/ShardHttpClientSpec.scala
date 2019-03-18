package com.salsify.lineserver.client.distribution

import com.salsify.helpers.BaseSpec

class ShardHttpClientSpec extends BaseSpec {

  private val HostA = createShardClient("hostA", 8080)
  private val HostADup = createShardClient("hostA", 8080)
  private val HostADifferentPort = createShardClient("hostA", 8081)
  private val HostB = createShardClient("hostB", 8080)

  it must "store the host and the port" in {
    HostA.host shouldEqual "hostA"
    HostA.port shouldEqual 8080
  }

  it must "validate correctly if two clients are equal and have the same hashcode" in {

    // Same host and port.
    HostA shouldEqual HostADup
    HostA.hashCode shouldEqual HostADup.hashCode

    // Same host but different port (e.g.: different containers within the same host).
    HostA should not equal HostADifferentPort
    HostA.hashCode should not equal HostADifferentPort.hashCode

    // Same port but different host.
    HostA should not equal HostB
    HostA.hashCode should not equal HostB.hashCode
  }
}
