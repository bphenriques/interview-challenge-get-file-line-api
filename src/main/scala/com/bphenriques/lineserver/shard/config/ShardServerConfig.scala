/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.shard.config

import com.bphenriques.lineserver.common.config.ServerBindingConfig
import com.bphenriques.lineserver.shard.ShardServer
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[ShardServer]].
  *
  * @param binding The binding configuration.
  */
final case class ShardServerConfig(binding: ServerBindingConfig)

/**
  * Companion class of [[ShardServerConfig]].
  */
object ShardServerConfig {

  import com.bphenriques.lineserver.common.enrichers.ConfigEnricher._

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
