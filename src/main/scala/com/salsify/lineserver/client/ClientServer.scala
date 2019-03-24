/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.client

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.config.ClientServerConfig
import com.salsify.lineserver.common.server.Server

import scala.concurrent.{ExecutionContext, Future}

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
  implicit val executionContext: ExecutionContext
) extends Server with ClientRoutes {

  override val host: String = config.binding.host

  override val port: Int = config.binding.port

  override def routes(): Route = healthRoute() ~ linesRoutes()

  override def createHandler(): ClientResource = new ClientResource(config.linesManager)

  /**
    * @inheritdoc
    *
    * If the lines supplier is not provided, then it is a secondary client, therefore no setup is required.
    */
  override def setup(): Future[Unit] = {
    config.linesSupplier match {
      case Some(linesSupplier) =>
        logger.info(s"Reading lines using $linesSupplier' ...")
        val numberOfLines = linesSupplier.size

        // Atomic counter to have a consistent progress counter.
        val processedLines: AtomicReference[Int] = new AtomicReference[Int](0)
        val linesUpload: Seq[Future[Unit]] = linesSupplier.getLines()
          .map {
            line => config.linesManager.setString(line.index, line.content)
              .map(_ => reportResult(numberOfLines, processedLines.updateAndGet(current => current + 1)))
          }

        Future.sequence(linesUpload)
          .map { _ =>
            logger.info(s"Processed ${processedLines.get()} lines. Server is now ready to start.")
          }
      case _ =>
        logger.info(s"No setup is required because this is not the primary client.")
        Future.successful()
    }
  }

  /**
    * Reports progress of file upload.
    *
    * @param numberOfLines            Total number of lines.
    * @param currentNumProcessedLines The current number of processed lines.
    * @param frequency                The reporting frequency. Defaults to 10%.
    */
  private def reportResult(numberOfLines: Int, currentNumProcessedLines: Int, frequency: Double = 0.10): Unit = {
    import com.salsify.lineserver.common.enrichers.DoubleEnricher._
    if ((currentNumProcessedLines % (numberOfLines * frequency)) ~= (0, precision = 1)) {
      val percentage = 1.0f * currentNumProcessedLines / numberOfLines
      logger.info(s"Processed $currentNumProcessedLines/$numberOfLines (${percentage * 100}%)")
    }
  }
}
