package com.salsify.lineserver.client.manager

import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.shard.Shard

import scala.concurrent.Future

/**
  * Interface to manage shards.
  */
trait ShardsManager extends Shard {

  /**
    * Perform any necessary setup operations.
    *
    * @param lineSupplier The line supplier.
    * @return The future when the setup has completed.
    */
  def setup(lineSupplier: LinesInputSupplier): Future[Unit]
}
