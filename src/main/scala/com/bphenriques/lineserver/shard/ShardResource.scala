/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.shard

import com.bphenriques.lineserver.shard.exception.KeyNotFoundException

import java.util.concurrent.locks.ReentrantLock
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/** Handles [[ShardRoutes]].
  *
  * @param executionContext The execution context.
  */
final class ShardResource(implicit val executionContext: ExecutionContext) extends Shard with LazyLogging {

  /** The set of lines stored in this shard.
    */
  private val keyValueMap: mutable.Map[Int, String] = mutable.Map()

  /** A write-lock to guard the keys.
    *
    * FIXME: It is not required as there are no updates in this project's use-case.
    * This was required due to a unknown error where the count() is not consistent with set of keys stored.
    * It is a odd by nature, however setString is only done in start-up time, therefore it does not impact the clients.
    *
    * BH-2 | Bruno Henriques (bphenriques@outlook.com@gmail.com)
    */
  private val lock = new ReentrantLock()

  override def getString(key: Int): Future[String] = Future {
    keyValueMap.get(key) match {
      case Some(line) => line
      case _          => throw KeyNotFoundException(key)
    }
  }

  override def setString(key: Int, value: String): Future[Unit] = Future {
    lock.lock()
    logger.trace(s"$key = '$value'")
    keyValueMap.put(key, value)
    lock.unlock()
  }

  override def count(): Future[Int] = Future {
    keyValueMap.keys.size
  }
}
