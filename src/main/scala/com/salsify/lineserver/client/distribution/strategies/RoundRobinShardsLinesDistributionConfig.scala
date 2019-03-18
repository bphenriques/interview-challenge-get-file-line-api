package com.salsify.lineserver.client.distribution.strategies

import com.salsify.lineserver.client.distribution.LinesDistributionConfig
import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

case class RoundRobinShardsLinesDistributionConfig(shards: List[HostConfig]) extends LinesDistributionConfig {
  require(shards.nonEmpty, "TODO")
}

object RoundRobinShardsLinesDistributionConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  import scala.collection.JavaConverters._

  def fromConfig(conf: Config): Try[RoundRobinShardsLinesDistributionConfig] = for {
    shards <- Try(conf.getConfigList("shards").asScala.map(_.read[HostConfig](HostConfig.fromConfig).get).toList)
  } yield RoundRobinShardsLinesDistributionConfig(shards)
}
