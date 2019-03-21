package com.salsify.lineserver.client.input.strategies

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.model.Line
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

/**
  * Supplies lines from a local file. Each line is delimited by a new line character.
  *
  * Initialization may be slow as it reads the number of lines from the file.
  *
  * @note This metadata is requires so that the Line Server client can reject requests before contacting the shard which
  *       is a network call (expensive). The alternative, is to know before hand the number of lines in the file (e.g.,
  *       through the configuration). However the benefits are limited as this is only done **once before** the server
  *       starts.
  *
  * @param config The configuration.
  */
final class LocalFileLinesInputSupplier(
  val config: LocalFileLinesInputSupplierConfig
) extends LinesInputSupplier with LazyLogging {

  /**
    * Storing the number of lines once.
    */
  override val size: Int = Source.fromFile(config.file).getLines().length

  /**
    * Returns a lazy stream of [[Line]].
    */
  override def getLines(): Seq[Line] = {
    logger.info(s"Reading '${config.file.getAbsolutePath}' ...")

    import com.salsify.lineserver.common.enrichers.IteratorEnricher._

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
