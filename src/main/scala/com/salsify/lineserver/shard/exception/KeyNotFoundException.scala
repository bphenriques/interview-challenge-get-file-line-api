package com.salsify.lineserver.shard.exception

import com.salsify.lineserver.common.exception.LineServerException

/**
  * Key not found exception.
  * @param key The key.
  */
case class KeyNotFoundException(key: Long) extends LineServerException(s"Key not found: '$key'")
