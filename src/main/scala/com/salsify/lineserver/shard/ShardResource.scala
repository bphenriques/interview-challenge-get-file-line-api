package com.salsify.lineserver.shard

import com.salsify.lineserver.shard.exception.KeyNotFoundException

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class ShardResource(implicit val executionContext: ExecutionContext) extends Shard {

  val keyValueMap: mutable.Map[Int, String] = mutable.Map()

  /**
    * Does not validate.
    *
    * @param key
    * @return
    */
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
