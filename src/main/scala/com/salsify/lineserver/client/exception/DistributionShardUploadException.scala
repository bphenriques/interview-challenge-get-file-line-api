package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

/**
  * Single shard upload exception.
  *
  * @param host       The shard's host.
  * @param port       The shard's port.
  * @param lineNumber The line number.
  * @param line       The content of the line.
  * @param error      The error.
  */
final case class DistributionShardUploadException(
  lineNumber: Int,
  line: String,
  error: Throwable = None.orNull
) extends LineServerException(s"Failed to upload line '$lineNumber': $line.", error)
