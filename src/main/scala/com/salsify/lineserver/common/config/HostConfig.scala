package com.salsify.lineserver.common.config

import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of a host. Either for binding or to to setup a remote resource.
  *
  * @param host A non-empty host.
  * @param port A port between 1023 and 65535
  * @see https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
  */
case class HostConfig(host: String, port: Int) {
  require(host.trim.length > 0, "The must be non-empty")
  require(port > 1023 && port <= 65535, "The port must be between 1023 and 65535")
}

/**
  * Companion object of [[HostConfig]].
  */
object HostConfig {

  /**
    * Creates an instance of [[HostConfig]].
    *
    * @param conf The config.
    * @return An instance of [[HostConfig]].
    */
  def fromConfig(conf: Config): Try[HostConfig] = for {
    host <- Try(conf.getString("host"))
    port <- Try(conf.getInt("port"))
  } yield HostConfig(host, port)
}
