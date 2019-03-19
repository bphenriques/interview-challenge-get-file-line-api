package com.salsify.lineserver.client.distribution

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.shard.Shard

import scala.concurrent.Future

/**
  * Interface to distribute lines and access them.
  */
trait LinesDistribution extends Shard {

  /**
    * Perform any necessary setup operations.
    *
    * @param lineSupplier The line supplier.
    * @return The future when the setup has completed.
    */
  def setup(lineSupplier: LinesInputSupplier): Future[Unit]
}
