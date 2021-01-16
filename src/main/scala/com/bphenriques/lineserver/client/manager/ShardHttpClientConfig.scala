/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.manager

import com.typesafe.config.Config

import scala.util.Try

/** Configuration of a shard client.
  *
  * @param host       A HTTP host that starts with http://.
  * @param port       A port between 1023 and 65535
  * @param queueSize  The internal queue size to handle requests during spikes. Defaults to 1024.
  * @see https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
  */
final case class ShardHttpClientConfig(host: String, port: Int, queueSize: Int) {
  require(host.trim.length > 0, "The must be non-empty")
  require(host.startsWith("http://"), "The host must start with http://")
  require(port > 1023 && port <= 65535, "The port must be between 1023 and 65535")
  require(queueSize > 0, "The requests queue should be greater or equal to zero")
}

/** Companion object of [[ShardHttpClientConfig]].
  */
object ShardHttpClientConfig {

  /** Creates an instance of [[ShardHttpClientConfig]].
    *
    * @param conf The config.
    * @return An instance of [[ShardHttpClientConfig]].
    */
  def from(conf: Config): Try[ShardHttpClientConfig] = for {
    host <- Try(conf.getString("host"))
    port <- Try(conf.getInt("port"))
    queueSize <- Try(conf.getInt("requests-queue-size"))
  } yield ShardHttpClientConfig(host, port, queueSize)
}
