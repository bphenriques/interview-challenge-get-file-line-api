/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.input

import com.bphenriques.lineserver.common.model.Line

trait LinesInputSupplierConfig

/** Supplies lines. E.g., offline or through an S3 bucket.
  */
trait LinesInputSupplier extends AutoCloseable {

  /** The sequence of lines.
    *
    * @return The sequence of lines.
    */
  def readLines(): Seq[Line]

  /** The number of lines in the file. This computation can be expensive, therefore store the result after usage.
    *
    * @return The number of lines in the file.
    */
  def size: Int = readLines().size
}
