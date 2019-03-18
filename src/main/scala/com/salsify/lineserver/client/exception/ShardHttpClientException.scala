package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

case class ShardHttpClientException(message: String) extends LineServerException(message)
