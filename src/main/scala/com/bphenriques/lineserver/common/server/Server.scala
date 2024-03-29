/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/** Abstraction of a Akka Server. The life cycle is: `Setup -> Start -> Shutdown (automatic)`:
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

  implicit val system: ActorSystem
  implicit val executionContext: ExecutionContext

  def host: String
  def port: Int

  protected def routesProvider(): RoutesProvider

  /** Perform any necessary operations before starting. Defaults to noop.
    */
  protected def setup(): Future[Unit] = Future.unit

  /** Setups and then starts the server. This operation is blocking until the process is closed.
    */
  final def start(): Try[Server] = Try {
    logger.info("Starting server...")
    val binding = setup()
      .flatMap(_ => Http().bindAndHandle(routesProvider().routes(), host, port))

    // Add coordinated shutdown that terminates the server gracefully on server shutdown. See notes on the class.
    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceUnbind, "http_shutdown") { () =>
      shutdown(binding)
    }

    // Verify if the server started successfully. Gracefully shutdown if not.
    binding.onComplete {
      case Success(bound) =>
        logger.info(s"Server available at ${bound.localAddress.getHostString}:${bound.localAddress.getPort}")
      case Failure(e) =>
        logger.error("Binding Failure. Shutting down the server.", e)
        shutdown(binding)
    }

    // Blocking the call.
    Await.result(system.whenTerminated, Duration.Inf)
    this
  }

  final def shutdown(binding: Future[Http.ServerBinding]): Future[Done] = {
    logger.info("Shutting down...")
    binding
      .flatMap(_.terminate(hardDeadline = 1.minute))
      .flatMap { _ => system.terminate() }
      .map { _ => Done }
  }
}
