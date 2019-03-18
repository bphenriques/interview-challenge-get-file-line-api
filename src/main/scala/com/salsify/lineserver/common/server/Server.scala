package com.salsify.lineserver.common.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

trait Server extends LazyLogging {

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer

  implicit val executionContext: ExecutionContext

  def host: String

  def port: Int

  def routes: Route

  def setup(): Try[Server] = Success(this)

  def start(): Try[Server] = Try {
    logger.info("Starting server...")
    val binding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, host, port)

    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseServiceUnbind, "http_shutdown") { () =>
      shutdown(binding)
    }

    binding.onComplete {
      case Success(bound) =>
        logger.info(s"Server available at ${bound.localAddress.getHostString}:${bound.localAddress.getPort}")
      case Failure(e) =>
        logger.error("Server failed to start", e)
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)
    this
  }

  /**
    * @see [[https://doc.akka.io/docs/akka-http/current/server-side/graceful-termination.html]]
    * @see [[https://discuss.lightbend.com/t/graceful-termination-on-sigterm-using-akka-http/1619]]
    * @see [[https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/QuickstartServer.scala]]
    */
  def shutdown(binding: Future[Http.ServerBinding]): Future[Done] = {
    logger.info("Shutting down...")
    binding
      .flatMap(_.terminate(hardDeadline = 1.minute))
      .flatMap { _ => system.terminate() }
      .map { _ => Done }
  }
}
