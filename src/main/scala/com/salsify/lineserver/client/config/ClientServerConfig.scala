package com.salsify.lineserver.client.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.{LinesDistribution, LinesDistributionFactory}
import com.salsify.lineserver.client.input.{LinesInputSupplier, LinesInputSupplierFactory}
import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.config.ServerConfig
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

case class ClientServerConfig(
  binding: HostConfig,
  lineSupplier: LinesInputSupplier,
  lineDistribution: LinesDistribution
) extends ServerConfig

object ClientServerConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

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



