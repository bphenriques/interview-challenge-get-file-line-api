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

/** Supplies lines from a local file. Each line is delimited by a new line character.
  *
  * @param config The configuration.
  */
final class LocalFileLinesInputSupplier(val config: LocalFileLinesInputSupplierConfig)
    extends LinesInputSupplier
    with LazyLogging {

  private val source = Source.fromFile(config.file)

  override def readLines(): Seq[Line] = {
    logger.info(s"Reading '${config.file.getAbsolutePath}' ...")

    // By requirement, the file is in ASCII. By default, the reader reads from UTF-8 which is a *super set* of ASCII.
    import com.bphenriques.lineserver.common.enrichers.IteratorEnricher._
    source.getLines()
      .zipWithIndex(1)
      .map { case (line, lineNumber) => Line(lineNumber, line) }
      .toStream // Is already lazy. This is to conform to the method signature.
  }

  override def toString: String = s"LocalFileLineSupplier('${config.file.getAbsolutePath})"

  override def close(): Unit = source.close()

  override def size: Int = {
    val source = Source.fromFile(config.file) // Can't read the lines without consuming the source. Just want to peek at the file.
    val result = source.getLines().size
    source.close()
    result
  }
}

/** Companion object of [[LocalFileLinesInputSupplier]].
  */
object LocalFileLinesInputSupplier {

  /** Creates an instance of [[LocalFileLinesInputSupplier]].
    *
    * @param config The configuration.
    * @return The instance of [[LocalFileLinesInputSupplier]].
    */
  def apply(config: LocalFileLinesInputSupplierConfig): LocalFileLinesInputSupplier = new LocalFileLinesInputSupplier(
    config
  )
}
