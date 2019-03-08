/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver.config

import com.typesafe.config.Config

import scala.util.Try

/**
  * The Application configuration.
  */
final case class AppConfig(master: Boolean)

/**
  * Companion class of [[AppConfig]].
  */
object AppConfig {

  /**
    * Creates a [[AppConfig]].
    *
    * @param conf The configuration.
    * @return The instance of [[AppConfig]].
    */
  def fromConfig(conf: Config): Try[AppConfig] = Try { AppConfig(true) }
}
