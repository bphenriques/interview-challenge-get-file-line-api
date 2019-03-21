package com.salsify.lineserver.client

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.config.ClientServerConfig
import com.salsify.lineserver.common.server.Server

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

/**
  * Creates a Client server.
  *
  * @param config             The configuration.
  * @param materializer       (implicit) The Akka actor materializer.
  * @param system             (implicit) The Akka actor system.
  * @param executionContext   (implicit) The execution context.
  */
final class ClientServer(config: ClientServerConfig)(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContext,
) extends Server with ClientRoutes {

  override val host: String = config.binding.host

  override val port: Int = config.binding.port

  override val routes: Route = healthRoute() ~ linesRoutes()

  /**
    * The strategy used to setup the cluster with lines.
    */
  private val lineSupplier = config.linesSupplier

  /**
    * The strategy used to distribute the lines across the cluster.
    */
  private val linesDistribution = config.linesDistribution

  override val handler: ClientResource = new ClientResource(lineSupplier, linesDistribution)

  override def setup(): Try[Unit] = Try {
    val fileUpload = linesDistribution.setup(lineSupplier)
    Await.result(fileUpload, Duration.Inf)
  }
}
