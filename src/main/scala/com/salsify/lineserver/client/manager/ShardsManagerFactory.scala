package com.salsify.lineserver.client.manager

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.manager.strategies.{RoundRobinShardManagerConfig, RoundRobinShardsManager}
import com.salsify.lineserver.common.exception.LineServerConfigException
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Factory of [[ShardsManager]].
  */
object ShardsManagerFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[ShardsManager]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[ShardsManager]].
    */
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[ShardsManager] = Try {
    conf.getString("type") match {
      case "shards-round-robin" => conf.getConfig("shards-round-robin")
        .read[RoundRobinShardManagerConfig](RoundRobinShardManagerConfig.from)
        .map(c => new RoundRobinShardsManager(c))
        .get

      case distribution => throw LineServerConfigException(s"Unknown distribution '$distribution'")
    }
  }
}
