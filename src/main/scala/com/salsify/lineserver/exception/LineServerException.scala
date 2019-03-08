/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver.exception

/**
  * Generic Line Server exception.
  *
  * @param message    The message.
  * @param error      The error.
  */
case class LineServerException(
  message: String,
  error: Throwable
) extends Exception(message, error)
