package com.salsify.lineserver.shard

import com.salsify.lineserver.shard.exception.KeyNotFoundException

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Handles [[ShardRoutes]].
  *
  * @param executionContext The execution context.
  */
class ShardResource(implicit val executionContext: ExecutionContext) extends Shard {

  /**
    * The set of lines stored in this shard.
    */
  private val keyValueMap: mutable.Map[Int, String] = mutable.Map()

  override def getInt(key: Int): Future[String] = Future {
    keyValueMap.get(key) match {
      case Some(line) => line
      case _          => throw KeyNotFoundException(key)
    }
  }

  override def setInt(key: Int, value: String): Future[Unit] = Future {
    keyValueMap.put(key, value)
  }
}
