/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.input.strategies

import com.bphenriques.lineserver.client.input.LinesInputSupplier
import com.bphenriques.lineserver.common.model.Line
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

/**
  * Supplies lines from a local file. Each line is delimited by a new line character.
  *
  * @param config The configuration.
  */
final class LocalFileLinesInputSupplier(
  val config: LocalFileLinesInputSupplierConfig
) extends LinesInputSupplier with LazyLogging {

  /**
    * Returns a lazy stream of [[Line]].
    */
  override def getLines(): Seq[Line] = {
    logger.info(s"Reading '${config.file.getAbsolutePath}' ...")

    import com.bphenriques.lineserver.common.enrichers.IteratorEnricher._

    // By requirement, the file is in ASCII. By default, the reader reads from UTF-8 which is a *super set* of ASCII.
    Source.fromFile(config.file).getLines()
      .zipWithIndex(1)
      .map { case (line, lineNumber) => Line(lineNumber, line) }
      .toStream // getLines() is already lazy. This is to conform to the method signature.
  }

  override def toString: String = s"LocalFileLineSupplier('${config.file.getAbsolutePath})"
}

/**
  * Companion object of [[LocalFileLinesInputSupplier]].
  */
object LocalFileLinesInputSupplier {

  /**
    * Creates an instance of [[LocalFileLinesInputSupplier]].
    *
    * @param config The configuration.
    * @return The instance of [[LocalFileLinesInputSupplier]].
    */
  def apply(config: LocalFileLinesInputSupplierConfig): LocalFileLinesInputSupplier = new LocalFileLinesInputSupplier(config)
}