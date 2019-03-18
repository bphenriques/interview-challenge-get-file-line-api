package com.salsify.lineserver.common.enrichers

object IteratorEnricher {
  implicit class RichIterator[T](iterator: Iterator[T]) {
    def zipWithIndex(start: Int): Iterator[(T, Int)] = iterator.zip(stream(start).toIterator)
  }

  private def stream(i: Int): Stream[Int] = i #:: stream(i + 1)
}
