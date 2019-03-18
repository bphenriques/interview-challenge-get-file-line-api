package com.salsify.lineserver.shard.config

import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.config.ServerConfig
import com.typesafe.config.Config

import scala.util.Try

case class ShardServerConfig(binding: HostConfig) extends ServerConfig

object ShardServerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  def fromConfig(conf: Config): Try[ShardServerConfig] = for {
    binding        <- conf.getConfig("http").read[HostConfig](HostConfig.fromConfig)
  } yield ShardServerConfig(binding)
}
