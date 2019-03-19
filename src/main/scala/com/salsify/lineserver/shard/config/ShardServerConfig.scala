package com.salsify.lineserver.shard.config

import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[com.salsify.lineserver.shard.ShardServer]].
  *
  * @param binding The binding configuration.
  */
case class ShardServerConfig(binding: HostConfig)

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
  def fromConfig(conf: Config): Try[ShardServerConfig] = for {
    binding        <- conf.getConfig("http").read[HostConfig](HostConfig.fromConfig)
  } yield ShardServerConfig(binding)
}
