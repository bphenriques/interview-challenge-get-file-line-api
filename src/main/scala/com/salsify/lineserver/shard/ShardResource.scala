package com.salsify.lineserver.shard

import java.util.concurrent.atomic.AtomicReference

import com.salsify.lineserver.shard.exception.KeyNotFoundException
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Handles [[ShardRoutes]].
  *
  * @param executionContext The execution context.
  */
final class ShardResource(implicit val executionContext: ExecutionContext) extends Shard with LazyLogging {

  /**
    * The set of lines stored in this shard.
    */
  private val keyValueMap: mutable.Map[Int, String] = mutable.Map()

  /**
    * Guarantees count consistency.
    */
  private val numberOfElements = new AtomicReference[Int](0)

  override def getString(key: Int): Future[String] = Future {
    keyValueMap.get(key) match {
      case Some(line) => line
      case _          => throw KeyNotFoundException(key)
    }
  }

  override def setString(key: Int, value: String): Future[Unit] = Future {
    logger.trace(s"$key = '$value'")
    keyValueMap.put(key, value)
    numberOfElements.updateAndGet(_ + 1)
  }

  override def count(): Future[Int] = Future { numberOfElements.get() }
}
