/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.exception

import com.bphenriques.lineserver.common.exception.LineServerException

/**
  * Line not found exception.
  *
  * @param lineNumber The line number.
  */
final case class LineNotFoundException(lineNumber: Int) extends LineServerException(s"Line not found: $lineNumber")

