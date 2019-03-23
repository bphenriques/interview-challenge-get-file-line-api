package com.salsify.lineserver.client

import com.salsify.lineserver.client.exception.LineNotFoundException
import com.salsify.lineserver.client.manager.LinesManager
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Handles [[ClientRoutes]].
  * <p>
  * It firstly obtains the number of available lines. This request timeouts after 30 seconds,
  * failing the server as consequence.
  *
  * @param linesManager  The strategy used to distribute the lines.
  */
final class ClientResource(linesManager: LinesManager) extends Client with LazyLogging {

  /**
    * Stores the number of available lines. Not accessing the manager itself b/c it is slower as it uses
    */
  private lazy val numberOfAvailableLines = {
    val result = Await.result(linesManager.count(), 30 seconds)
    logger.info(s"Read $result from the lines manager.")
    result
  }

  override def get(lineNumber: Int): Future[String] =
    if (lineNumber <= 0 || lineNumber > numberOfAvailableLines) {
      Future.failed(LineNotFoundException(lineNumber))
    } else {
      linesManager.getString(lineNumber)
    }
}
