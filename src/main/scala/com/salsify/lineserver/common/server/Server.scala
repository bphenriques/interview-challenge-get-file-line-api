/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.common.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Abstraction of a Akka Server. The life cycle is: `Setup -> Start -> Shutdown (automatic)`:
  * <ul>
  *   <li>`Setup`: Perform any necessary operations before starting. Defaults to noop.</li>
  *   <li>`Start`: Start the server. This operation is blocking.</li>
  *   <li>`Shutdown`: Shutdowns the system with the JVM.</li>
  * </ul>
  *
  * @see [[https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/QuickstartServer.scala]]
  * @see [[https://doc.akka.io/docs/akka-http/current/server-side/graceful-termination.html]]
  * @see [[https://discuss.lightbend.com/t/graceful-termination-on-sigterm-using-akka-http/1619]]
  */
trait Server extends LazyLogging {

  /**
    * The Akka actor system.
    */
  implicit val system: ActorSystem

  /**
    * The Akka actor materializer.
    */
  implicit val materializer: ActorMaterializer

  /**
    * The execution context.
    */
  implicit val executionContext: ExecutionContext

  /**
    * The server's binding host.
    */
  def host: String

  /**
    * The server's binding port.
    */
  def port: Int

  /**
    * The server's routes.
    */
  protected def routes(): Route

  /**
    * Perform any necessary operations before starting. Defaults to noop.
    */
  protected def setup(): Future[Unit] = Future.unit

  /**
    * Starts the server. This operation is blocking until the process is closed.
    */
  final def start(): Try[Server] = Try {
    logger.info("Starting server...")
    val binding = setup().flatMap(_ => Http().bindAndHandle(routes(), host, port))

    // Add coordinated shutdown that terminates the server gracefully on server shutdown. See notes on the class.
    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseServiceUnbind, "http_shutdown") { () =>
      shutdown(binding)
    }

    // Verify if the server started successfully. Gracefully shutdown if not.
    binding.onComplete {
      case Success(bound) =>
        logger.info(s"Server available at ${bound.localAddress.getHostString}:${bound.localAddress.getPort}")
      case Failure(e) =>
        logger.error("Server failed to start", e)
        shutdown(binding)
    }

    // Blocking the call
    Await.result(system.whenTerminated, Duration.Inf)
    this
  }

  /**
    * Shutdowns Akka server.
    *
    * @param binding The server binding.
    * @return Future that signals completion.
    */
  final def shutdown(binding: Future[Http.ServerBinding]): Future[Done] = {
    logger.info("Shutting down...")
    binding
      .flatMap(_.terminate(hardDeadline = 1.minute))
      .flatMap { _ => system.terminate() }
      .map { _ => Done }
  }
}
