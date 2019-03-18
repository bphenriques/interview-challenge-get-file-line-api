package com.salsify.lineserver.shard.exception

import com.salsify.lineserver.common.exception.LineServerException

case class KeyNotFoundException(key: Long) extends LineServerException(s"Key not found: '$key'")
