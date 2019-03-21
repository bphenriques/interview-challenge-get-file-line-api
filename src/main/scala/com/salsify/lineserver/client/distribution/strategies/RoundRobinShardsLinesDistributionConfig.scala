package com.salsify.lineserver.client.distribution.strategies

import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[RoundRobinShardsLinesDistribution]].
  *
  * @param shards The shard locations.
  */
final case class RoundRobinShardsLinesDistributionConfig(shards: List[HostConfig]) {
  require(shards.nonEmpty, "The shards must be a non-empty list.")
}

/**
  * Companion class of [[RoundRobinShardsLinesDistributionConfig]]
  */
object RoundRobinShardsLinesDistributionConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._
  import scala.collection.JavaConverters._

  /**
    * Creates an instance of [[RoundRobinShardsLinesDistributionConfig]].
    *
    * @param conf The configuration.
    * @return An instance of [[RoundRobinShardsLinesDistributionConfig]].
    */
  def from(conf: Config): Try[RoundRobinShardsLinesDistributionConfig] = for {
    shards <- Try(conf.getConfigList("shards").asScala.map(_.read[HostConfig](HostConfig.from).get).toList)
  } yield RoundRobinShardsLinesDistributionConfig(shards)
}
