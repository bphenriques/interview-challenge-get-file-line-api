package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

case class ShardUploadException(
  host: String,
  port: Int,
  lineNumber: Long,
  line: String,
  error: Throwable = None.orNull
) extends LineServerException(s"Shard '$host:$port' failed. Failed to upload line '$lineNumber': $line.", error)
