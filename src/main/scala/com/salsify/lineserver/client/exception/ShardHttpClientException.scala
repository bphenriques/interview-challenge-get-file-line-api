package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

/**
  * Shard Http Client exception.
  *
  * @param message  The message.
  */
case class ShardHttpClientException(message: String) extends LineServerException(message)
