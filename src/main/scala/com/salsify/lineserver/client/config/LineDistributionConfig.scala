package com.salsify.lineserver.client.config

import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.util.Try

case class LineDistributionConfig(shards: List[HostConfig]){
  require(shards.nonEmpty, "TODO")
}

object LineDistributionConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  import scala.collection.JavaConverters._

  def fromConfig(conf: Config): Try[LineDistributionConfig] = for {
    shards <- Try(conf.getConfigList("shards").asScala.map(_.read[HostConfig](HostConfig.fromConfig).get).toList)
  } yield LineDistributionConfig(shards)
}
