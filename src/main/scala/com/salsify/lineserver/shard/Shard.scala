/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.shard

import com.salsify.lineserver.shard.exception.KeyNotFoundException

import scala.concurrent.Future

/**
  * The shards API.
  */
trait Shard {

  /**
    * Gets the stored String value given key.
    *
    * @param key The key.
    * @return The future that will execute the operation and return the String.
    * @throws KeyNotFoundException If the key does not exist.
    */
  def getString(key: Int): Future[String]

  /**
    * Inserts a key-value pair.
    *
    * @param key The key.
    * @param value The value.
    * @return The future that will execute the operation.
    */
  def setString(key: Int, value: String): Future[Unit]

  /**
    * Returns the number of values stored. It is not thread-safe, therefore the values may be inconsistent.
    *
    * @return The future with the number of va rows.
    */
  def count(): Future[Int]
}
