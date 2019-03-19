package com.salsify.lineserver.shard

import com.salsify.lineserver.shard.exception.KeyNotFoundException

import scala.concurrent.Future

/**
  * The shards API.
  */
trait Shard {

  /**
    * Gets an integer.
    *
    * @param key The key.
    * @return The future that will execute the operation and return the String.
    * @throws KeyNotFoundException If the key does not exist.
    */
  def getInt(key: Int): Future[String]

  /**
    * Inserts a key-value pair.
    *
    * @param key The key.
    * @param value The value.
    * @return The future that will execute the operation.
    */
  def setInt(key: Int, value: String): Future[Unit]
}
