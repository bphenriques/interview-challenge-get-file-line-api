package com.salsify.lineserver.client.exception

import com.salsify.lineserver.client.manager.ShardHttpClient
import com.salsify.lineserver.common.exception.LineServerException

/**
  * Shard Http Client exception.
  *
  * @param message  The message.
  */
final case class ShardHttpClientException(shard: ShardHttpClient, message: String)
  extends LineServerException(s"Shard@${shard.host}:${shard.port}: $message")
