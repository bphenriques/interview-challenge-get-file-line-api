/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.common.config

import com.typesafe.config.Config

import scala.util.Try

/**
  * Binding configuration.
  *
  * @param host A non-empty host.
  * @param port A port between 1023 and 65535
  * @see https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
  */
final case class ServerBindingConfig(host: String, port: Int) {
  require(host.trim.length > 0, "The must be non-empty")
  require(port > 1023 && port <= 65535, "The port must be between 1023 and 65535")
}

/**
  * Companion object of [[ServerBindingConfig]].
  */
object ServerBindingConfig {

  /**
    * Creates an instance of [[ServerBindingConfig]].
    *
    * @param conf The config.
    * @return An instance of [[ServerBindingConfig]].
    */
  def from(conf: Config): Try[ServerBindingConfig] = for {
    host      <- Try(conf.getString("host"))
    port      <- Try(conf.getInt("port"))
  } yield ServerBindingConfig(host, port)
}
