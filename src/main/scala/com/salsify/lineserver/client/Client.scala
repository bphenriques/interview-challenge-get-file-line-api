package com.salsify.lineserver.client

import scala.concurrent.Future

trait Client {
  def get(lineNumber: Int): Future[String]
}
