package com.salsify.lineserver.client.input

import com.salsify.lineserver.common.model.Line

trait LinesInputSupplierConfig

/**
  * Supplies lines. E.g., offline or through an S3 bucket.
  */
trait LinesInputSupplier {

  /**
    * The sequence of lines.
    *
    * @return The sequence of lines.
    */
  def getLines(): Seq[Line]

  /**
    * The number of lines in the file. This value should be cached and not re-computed.
    *
    * @return The number of lines in the file.
    */
  def size: Int
}
