package com.salsify.lineserver.client.manager.strategies

import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[RoundRobinShardsManager]].
  *
  * @param shards The shard locations.
  */
final case class RoundRobinShardManagerConfig(shards: List[HostConfig]) {
  require(shards.nonEmpty, "The shards must be a non-empty list.")
}

/**
  * Companion class of [[RoundRobinShardManagerConfig]]
  */
object RoundRobinShardManagerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._
  import scala.collection.JavaConverters._

  /**
    * Creates an instance of [[RoundRobinShardManagerConfig]].
    *
    * @param conf The configuration.
    * @return An instance of [[RoundRobinShardManagerConfig]].
    */
  def from(conf: Config): Try[RoundRobinShardManagerConfig] = for {
    shards <- Try(conf.getConfigList("shards").asScala.map(_.read[HostConfig](HostConfig.from).get).toList)
  } yield RoundRobinShardManagerConfig(shards)
}
