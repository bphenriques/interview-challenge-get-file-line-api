package com.salsify.lineserver.client.input

import com.salsify.lineserver.common.model.Line

trait LinesInputSupplierConfig

trait LinesInputSupplier {

  def getLines(): Seq[Line]

  def size: Int

  def description: String
}
