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
    * The number of lines in the file. This computation can be expensive, therefore store the result after usage.
    *
    * @return The number of lines in the file.
    */
  def size: Int = getLines().size
}

