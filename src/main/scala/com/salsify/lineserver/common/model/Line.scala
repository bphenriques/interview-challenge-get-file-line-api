package com.salsify.lineserver.common.model

/**
  * Represents an line.
  *
  * @param index    The positive line index.
  * @param content  The content of the line.
  */
final case class Line(index: Int, content: String) {
  require(index > 0)
}
