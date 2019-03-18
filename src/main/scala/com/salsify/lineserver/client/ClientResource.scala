package com.salsify.lineserver.client

import akka.actor.ActorSystem
import com.salsify.lineserver.client.distribution.LinesDistribution
import com.salsify.lineserver.client.exception.LineNotFoundException
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class ClientResource(val linesInputSupplier: LinesInputSupplier, val linesDistribution: LinesDistribution)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends Client with LazyLogging {

  /**
    * Does not validate.
    *
    * @param lineNumber
    * @return
    */
  override def get(lineNumber: Int): Future[String] =
    if (lineNumber <= 0 || lineNumber > linesInputSupplier.size) {
      Future.failed(LineNotFoundException(lineNumber))
    } else {
      linesDistribution.getInt(lineNumber)
    }
}
