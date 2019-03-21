package com.salsify.lineserver.client.distribution

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.strategies.{RoundRobinShardsLinesDistribution, RoundRobinShardsLinesDistributionConfig}
import com.salsify.lineserver.common.exception.LineServerConfigException
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Factory of [[LinesDistribution]].
  */
object LinesDistributionFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[LinesDistribution]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[LinesDistribution]].
    */
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[LinesDistribution] = Try {
    conf.getString("type") match {
      case "shards-round-robin" => conf.getConfig("shards-round-robin")
        .read[RoundRobinShardsLinesDistributionConfig](RoundRobinShardsLinesDistributionConfig.from)
        .map(c => new RoundRobinShardsLinesDistribution(c))
        .get

      case distribution => throw LineServerConfigException(s"Unknown distribution '$distribution'")
    }
  }
}
