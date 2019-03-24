/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client.manager

import com.salsify.lineserver.shard.Shard
import com.salsify.lineserver.shard.exception.KeyNotFoundException

import scala.concurrent.Future

/**
  * Interface to manage shards. This interface is similar to [[Shard]].
  */
trait LinesManager {

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
    * The number of available lines. This allows new clients to join and know the number of lines available without
    * knowing anything about the source file.
    *
    * @return The number of available lines.
    */
  def count(): Future[Int]
}
