package com.salsify.lineserver.client.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.input.{LinesInputSupplier, LinesInputSupplierFactory}
import com.salsify.lineserver.client.manager.{ShardsManager, ShardsManagerFactory}
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
final case class ClientServerConfig(
  binding: HostConfig,
  linesSupplier: LinesInputSupplier,
  linesDistribution: ShardsManager
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
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[ClientServerConfig] = for {
    httpConfig        <- conf.getConfig("http").read[HostConfig](HostConfig.from)
    lineSupplier      <- LinesInputSupplierFactory.from(conf.getConfig("input"))
    lineDistribution  <- ShardsManagerFactory.from(conf.getConfig("manager"))
  } yield ClientServerConfig(httpConfig, lineSupplier, lineDistribution)
}



