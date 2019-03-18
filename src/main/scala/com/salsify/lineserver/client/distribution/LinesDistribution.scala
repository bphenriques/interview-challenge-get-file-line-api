package com.salsify.lineserver.client.distribution

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.shard.Shard

import scala.concurrent.Future

trait LinesDistributionConfig
trait LinesDistribution extends Shard {
  def description: String

  def setup(lineSupplier: LinesInputSupplier): Future[Unit]
}
