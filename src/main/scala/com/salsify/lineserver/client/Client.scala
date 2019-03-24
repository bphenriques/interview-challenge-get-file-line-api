/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client

import com.salsify.lineserver.client.exception.LineNotFoundException

import scala.concurrent.Future

/**
  * The client's API.
  */
trait Client {

  /**
    * Returns the content of line at `lineNumber`. It fails if the requested line is beyond the end of the file.
    *
    * @param lineNumber The line number.
    * @return The content of the line.
    * @throws LineNotFoundException if the line number is out of range.
    */
  def get(lineNumber: Int): Future[String]
}
