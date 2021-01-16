/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.exception

import com.bphenriques.lineserver.client.manager.ShardHttpClient
import com.bphenriques.lineserver.common.exception.LineServerException

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
