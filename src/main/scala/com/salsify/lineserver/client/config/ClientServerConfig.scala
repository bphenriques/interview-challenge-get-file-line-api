package com.salsify.lineserver.client.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.{LinesDistribution, LinesDistributionFactory}
import com.salsify.lineserver.client.input.{LinesInputSupplier, LinesInputSupplierFactory}
import com.salsify.lineserver.common.config.HostConfig
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Configuration of [[com.salsify.lineserver.client.ClientServer]]
  *
  * @param binding            The akka http binding.
  * @param linesSupplier      The lines supplier.
  * @param linesDistribution  The strategy used to distribute the lines.
  */
case class ClientServerConfig(
  binding: HostConfig,
  linesSupplier: LinesInputSupplier,
  linesDistribution: LinesDistribution
)

/**
  * Companion class of [[ClientServerConfig]].
  */
object ClientServerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[ClientServerConfig]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[ClientServerConfig]].
    */
  def fromConfig(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[ClientServerConfig] = for {
    httpConfig        <- conf.getConfig("http").read[HostConfig](HostConfig.fromConfig)
    lineSupplier      <- LinesInputSupplierFactory.from(conf.getConfig("input"))
    lineDistribution  <- LinesDistributionFactory.from(conf.getConfig("distribution"))
  } yield ClientServerConfig(httpConfig, lineSupplier, lineDistribution)
}



