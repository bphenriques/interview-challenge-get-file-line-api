package com.salsify.lineserver.shard

import scala.concurrent.Future

trait Shard {

  def getInt(key: Int): Future[String]

  def setInt(key: Int, value: String): Future[Unit]
}
