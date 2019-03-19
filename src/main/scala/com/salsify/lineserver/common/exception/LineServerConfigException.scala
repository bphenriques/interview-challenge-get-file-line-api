package com.salsify.lineserver.common.exception

/**
  * Line Server configuration exception.
  *
  * @param message    The message.
  */
case class LineServerConfigException(message: String) extends LineServerException(message)
