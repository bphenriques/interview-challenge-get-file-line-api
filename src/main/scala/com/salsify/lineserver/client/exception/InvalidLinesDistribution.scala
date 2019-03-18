package com.salsify.lineserver.client.exception

import com.salsify.lineserver.common.exception.LineServerException

case class InvalidLinesDistribution(distribution: String)
  extends LineServerException(s"Unrecognized line distributer '$distribution")

