package com.salsify.lineserver.common.config

import com.typesafe.config.Config

import scala.util.Try

/**
  *
  * @see https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
  * @param host
  * @param port
  */
case class HostConfig(host: String, port: Int) {
  require(host.trim.length > 0, "TODO")
  require(port > 1023 && port <= 65535, "TODO")
}

object HostConfig {
  def fromConfig(conf: Config): Try[HostConfig] = for {
    host <- Try(conf.getString("host"))
    port <- Try(conf.getInt("port"))
  } yield HostConfig(host, port)
}
