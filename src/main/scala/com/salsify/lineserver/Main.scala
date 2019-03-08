/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver

import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

/**
  * Main application that receives the path to a file as its single argument.
  * <p>
  * Exits with status code `0` if successful or `-1` otherwise.
  */
object Main extends App with Application with LazyLogging {

  if (args.length != 1) {
    println("Usage: <file>")
    System.exit(-1)
  }

  // Process arguments.
  val filename = args(0)

  val statusCode = process(filename) match {
    case Success(_) => 0
    case Failure(exception) =>
      logger.error(s"Error reading file $filename.", exception)
      -1
  }

  sys.exit(statusCode)
}

/**
  * Main application.
  */
trait Application {

  /**
    * TBD.
    *
    * @param filename The file to process.
    */
  def process(filename: String): Try[Unit] = Try {

  }
}
