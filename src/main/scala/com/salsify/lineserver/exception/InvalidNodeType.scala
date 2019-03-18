package com.salsify.lineserver.exception

import com.salsify.lineserver.common.exception.LineServerException

case class InvalidNodeType(nodeType: String) extends LineServerException(s"Unrecognized node type '$nodeType'")
