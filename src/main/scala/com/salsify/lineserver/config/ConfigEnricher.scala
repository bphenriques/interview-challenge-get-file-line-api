package com.salsify.lineserver.config

import com.typesafe.config.Config

import scala.util.Try

/**
  * Extension of [[com.typesafe.config.Config]].
  */
object ConfigEnricher {
  implicit class RichConfig(config: Config) {

    /**
      * Given a [[com.typesafe.config.Config]] create an instance of [[T]].
      *
      * @param parse The parser.
      * @tparam T    The target type.
      * @return Instance of [[T]]
      */
    def read[T](implicit parse: Config => Try[T]): Try[T] = parse(config)
  }
}
