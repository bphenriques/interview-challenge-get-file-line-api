/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client.manager

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.manager.strategies.{RoundRobinLinesManager, RoundRobinLinesManagerConfig}
import com.salsify.lineserver.common.exception.LineServerConfigException
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Factory of [[LinesManager]].
  */
object LinesManagerFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[LinesManager]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[LinesManager]].
    */
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[LinesManager] = Try {
    conf.getString("type") match {
      case "shards-round-robin" => conf.getConfig("shards-round-robin")
        .read[RoundRobinLinesManagerConfig](RoundRobinLinesManagerConfig.from)
        .map(c => new RoundRobinLinesManager(c))
        .get

      case distribution => throw LineServerConfigException(s"Unknown distribution '$distribution'")
    }
  }
}
