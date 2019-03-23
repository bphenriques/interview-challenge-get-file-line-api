package com.salsify.lineserver.client.manager.strategies

import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[RoundRobinLinesManager]].
  *
  * @param shards                   The shard locations.
  */
final case class RoundRobinLinesManagerConfig(shards: List[HostConfig]) {
  require(shards.nonEmpty, "The shards must be a non-empty list.")
}

/**
  * Companion class of [[RoundRobinLinesManagerConfig]]
  */
object RoundRobinLinesManagerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._
  import scala.collection.JavaConverters._

  /**
    * Creates an instance of [[RoundRobinLinesManagerConfig]].
    *
    * @param conf The configuration.
    * @return An instance of [[RoundRobinLinesManagerConfig]].
    */
  def from(conf: Config): Try[RoundRobinLinesManagerConfig] = for {
    shards               <- Try(conf.getConfigList("shards").asScala.map(_.read[HostConfig](HostConfig.from).get).toList)
  } yield RoundRobinLinesManagerConfig(shards)
}
