package com.salsify.lineserver.client.distribution

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.strategies.{RoundRobinShardsLinesDistribution, RoundRobinShardsLinesDistributionConfig}
import com.salsify.lineserver.client.exception.InvalidLinesDistribution
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

object LinesDistributionFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[LinesDistribution] = Try {
    conf.getString("type") match {
      case "shards-round-robin" => conf.getConfig("shards-round-robin")
        .read[RoundRobinShardsLinesDistributionConfig](RoundRobinShardsLinesDistributionConfig.fromConfig)
        .map(c => new RoundRobinShardsLinesDistribution(c))
        .get

      case distributer => throw InvalidLinesDistribution(distributer)
    }
  }
}
