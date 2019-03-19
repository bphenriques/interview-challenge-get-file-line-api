package com.salsify.lineserver.client.input.strategies

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.model.Line
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

/**
  * Supplies lines from a local file. Each line is delimited by a new line character.
  *
  * @param config The configuration.
  */
class LocalFileLinesInputSupplier(val config: LocalFileLinesInputSupplierConfig) extends LinesInputSupplier with LazyLogging {

  override val size: Int = Source.fromFile(config.file).getLines().length

  override def getLines(): Seq[Line] = {
    logger.info(s"Reading '${config.file.getAbsolutePath}' ...")

    import com.salsify.lineserver.common.enrichers.IteratorEnricher._
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
  def apply(config: LocalFileLinesInputSupplierConfig): LocalFileLinesInputSupplier = new LocalFileLinesInputSupplier(config)
}
