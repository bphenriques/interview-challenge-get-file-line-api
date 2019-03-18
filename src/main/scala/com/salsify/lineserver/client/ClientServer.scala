package com.salsify.lineserver.client

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.config.ClientServerConfig
import com.salsify.lineserver.common.server.Server

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

class ClientServer(config: ClientServerConfig)(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContext,
) extends Server with ClientRoutes {

  override val host: String = config.binding.host
  override val port: Int = config.binding.port

  override val routes: Route = healthRoute() ~ linesRoutes()

  private val lineSupplier = config.lineSupplier

  private val linesDistribution = config.lineDistribution

  override val handler: ClientResource = new ClientResource(lineSupplier, linesDistribution)

  override def setup(): Try[Server] = Try {
    val fileUpload = linesDistribution.setup(lineSupplier)
    Await.result(fileUpload, Duration.Inf)

    this
  }
}
