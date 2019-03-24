/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

/**
  * Line not found exception.
  *
  * @param lineNumber The line number.
  */
final case class LineNotFoundException(lineNumber: Int) extends LineServerException(s"Line not found: $lineNumber")

