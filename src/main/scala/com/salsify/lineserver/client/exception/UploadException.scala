package com.salsify.lineserver.client.exception

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.exception.LineServerException

case class UploadException(
  lineSupplier: LinesInputSupplier,
  error: Throwable = None.orNull
) extends LineServerException(s"Failed to start server with ${lineSupplier.description}", error)
