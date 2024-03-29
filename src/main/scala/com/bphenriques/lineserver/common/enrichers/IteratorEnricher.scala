/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.enrichers

/** Extension of [[Iterator[T]]].
  */
object IteratorEnricher {
  implicit class RichIterator[T](iterator: Iterator[T]) {

    /** Given a [[Iterator[T]] create a stream of pairs `([[T]], index)`.
      *
      * @param start The starting index.
      * @return The stream of pairs `([[T]], index)`.
      */
    def zipWithIndex(start: Int): Iterator[(T, Int)] = iterator.zip(Iterator.from(start))
  }
}
