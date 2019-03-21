package com.salsify.lineserver.client.exception

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.client.manager.ShardsManager
import com.salsify.lineserver.common.exception.LineServerException

/**
  * Distribution exception.
  *
  * @param linesSupplier      The lines supplier.
  * @param linesDistribution  The strategy used to distribute the lines.
  * @param error              The error.
  */
final case class DistributionException(
  linesSupplier: LinesInputSupplier,
  linesDistribution: ShardsManager,
  error: Throwable = None.orNull
) extends LineServerException(s"Failed to start server with $linesSupplier and $linesDistribution", error)
