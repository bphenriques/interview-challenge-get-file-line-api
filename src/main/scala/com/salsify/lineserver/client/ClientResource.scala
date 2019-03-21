package com.salsify.lineserver.client

import com.salsify.lineserver.client.distribution.LinesDistribution
import com.salsify.lineserver.client.exception.LineNotFoundException
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/**
  * Handles [[ClientRoutes]].
  *
  * @param linesInputSupplier The lines supplier.
  * @param linesDistribution  The strategy used to distribute the lines.
  */
final class ClientResource(
  linesInputSupplier: LinesInputSupplier,
  linesDistribution: LinesDistribution
) extends Client with LazyLogging {

  override def get(lineNumber: Int): Future[String] =
    if (lineNumber <= 0 || lineNumber > linesInputSupplier.size) {
      Future.failed(LineNotFoundException(lineNumber))
    } else {
      linesDistribution.getInt(lineNumber)
    }
}
