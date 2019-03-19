/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.config.AppConfig
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

/**
  * Main application that receives the path to a file as its single argument.
  * <p>
  * Exits with status code `0` if successful or `-1` otherwise.
  */
object Main extends App with Application with LazyLogging {

  val statusCode = start() match {
    case Success(_) => 0
    case Failure(exception) =>
      logger.error(s"Error while running application: ", exception)
      -1
  }

  sys.exit(statusCode)
}

/**
  * Main application.
  */
trait Application {

  /**
    * The Akka actor system.
    */
  implicit val system: ActorSystem = ActorSystem("LineServer")

  /**
    * The Akka actor materializer.
    */
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  /**
    * The execution context.
    */
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Starts the application.
    */
  def start(): Try[_] = ConfigFactory.load().read[AppConfig](AppConfig.fromConfig)
    .map(_.server)
    .flatMap(_.setup())
    .flatMap(_.start())
}
