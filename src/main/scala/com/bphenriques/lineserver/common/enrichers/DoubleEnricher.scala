/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.enrichers

/**
  * Extension of [[Double]].
  */
object DoubleEnricher {
  implicit class RichConfig(x: Double) {

    /**
      * Determines if `this` is more or less equal to `y` given the precision provided.
      *
      * @param y         The other value.
      * @param precision The precision. Defaults to 0.05.
      * @return If `x` and `y` are within `precision` difference.
      */
    def ~=(y: Double, precision: Double): Boolean = (x - y).abs < precision
  }
}
