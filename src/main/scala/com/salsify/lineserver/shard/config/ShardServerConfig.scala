/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.shard.config

import com.salsify.lineserver.common.config.ServerBindingConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[com.salsify.lineserver.shard.ShardServer]].
  *
  * @param binding The binding configuration.
  */
final case class ShardServerConfig(binding: ServerBindingConfig)

/**
  * Companion class of [[ShardServerConfig]].
  */
object ShardServerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[ShardServerConfig]].
    *
    * @param conf The configuration.
    * @return An instance of [[ShardServerConfig]].
    */
  def from(conf: Config): Try[ShardServerConfig] = for {
    binding        <- conf.getConfig("http").read[ServerBindingConfig](ServerBindingConfig.from)
  } yield ShardServerConfig(binding)
}
