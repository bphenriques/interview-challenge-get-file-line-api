package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

case class LineNotFoundException(lineNumber: Long) extends LineServerException(s"Line not found: $lineNumber")

