package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

case class InvalidLineSupplier(lineSupplier: String) extends LineServerException(s"Unrecognized line supplier '$lineSupplier")

