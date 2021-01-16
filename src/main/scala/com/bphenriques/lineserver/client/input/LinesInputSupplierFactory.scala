/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.input

import com.bphenriques.lineserver.client.input.strategies.{LocalFileLinesInputSupplier, LocalFileLinesInputSupplierConfig}
import com.bphenriques.lineserver.common.exception.LineServerConfigException
import com.bphenriques.lineserver.client.input.strategies.LocalFileLinesInputSupplierConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Factory of [[LinesInputSupplier]].
  */
object LinesInputSupplierFactory {

  import com.bphenriques.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[LinesInputSupplier]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @return An instance of [[LinesInputSupplier]].
    */
  def from(conf: Config): Try[LinesInputSupplier] = Try {
    conf.getString("type") match {
      case "local-file" => conf.getConfig("local-file")
        .read[LocalFileLinesInputSupplierConfig](LocalFileLinesInputSupplierConfig.from)
        .map(LocalFileLinesInputSupplier.apply)
        .get

      case supplier => throw LineServerConfigException(s"Unknown supplier '$supplier'")
    }
  }
}
