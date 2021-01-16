/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client

import com.bphenriques.lineserver.client.exception.LineNotFoundException
import com.bphenriques.lineserver.client.manager.LinesManager
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/** Handles [[ClientRoutes]].
  * <p>
  * It firstly obtains the number of available lines. This request timeouts after 30 seconds,
  * failing the server as consequence.
  *
  * @param linesManager  The strategy used to distribute the lines.
  */
final class ClientResource(linesManager: LinesManager) extends Client with LazyLogging {

  /** Stores the number of available lines. It is lazy b/c the secondary server does not know the number of lines.
    */
  private val numberOfAvailableLines = {
    val result = Await.result(linesManager.count(), 30.seconds)
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
