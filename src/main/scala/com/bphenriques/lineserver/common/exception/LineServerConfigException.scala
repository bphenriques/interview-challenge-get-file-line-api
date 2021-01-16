/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.exception

/**
  * Line Server configuration exception.
  *
  * @param message    The message.
  */
case class LineServerConfigException(message: String) extends LineServerException(message)
