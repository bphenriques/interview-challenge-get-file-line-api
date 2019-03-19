package com.salsify.lineserver.client.exception

import com.salsify.lineserver.client.distribution.LinesDistribution
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.exception.LineServerException

/**
  * Distribution exception.
  *
  * @param linesSupplier      The lines supplier.
  * @param linesDistribution  The strategy used to distribute the lines.
  * @param error              The error.
  */
case class DistributionException(
  linesSupplier: LinesInputSupplier,
  linesDistribution: LinesDistribution,
  error: Throwable = None.orNull
) extends LineServerException(s"Failed to start server with $linesSupplier and $linesDistribution", error)
