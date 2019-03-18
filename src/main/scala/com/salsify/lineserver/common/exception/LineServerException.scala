/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver.common.exception

/**
  * Generic Line Server exception.
  *
  * @param message    The message.
  * @param error      The error.
  */
class LineServerException(
  message: String,
  error: Throwable = None.orNull
) extends Exception(message, error)
