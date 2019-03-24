package com.salsify.lineserver.client.exception

import com.salsify.lineserver.client.manager.ShardHttpClient
import com.salsify.lineserver.common.exception.LineServerException

/**
  * Shard Http Client exception.
  *
  * @param shard    The shard.
  * @param message  The message.
  * @param error    Optional error.
  */
final case class ShardHttpClientException(
  shard: ShardHttpClient,
  message: String,
  error: Throwable = None.orNull
) extends LineServerException(s"Shard@${shard.host}:${shard.port}: $message", error)
