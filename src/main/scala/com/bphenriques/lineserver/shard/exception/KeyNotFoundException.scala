/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.shard.exception

import com.bphenriques.lineserver.common.exception.LineServerException

/** Key not found exception.
  *
  * @param key The key.
  */
final case class KeyNotFoundException(key: Int) extends LineServerException(s"Key not found: '$key'")
