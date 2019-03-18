package com.salsify.lineserver.client.input

import com.salsify.lineserver.client.exception.InvalidLineSupplier
import com.salsify.lineserver.client.input.strategies.{LocalFileLinesInputSupplier, LocalFileLinesInputSupplierConfig}
import com.typesafe.config.Config

import scala.util.Try

object LinesInputSupplierFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._
  def from(conf: Config): Try[LinesInputSupplier] = Try {
    conf.getString("type") match {
      case "local-file" => conf.getConfig("local-file")
        .read[LocalFileLinesInputSupplierConfig](LocalFileLinesInputSupplierConfig.fromConfig)
        .map(LocalFileLinesInputSupplier.fromConfig)
        .get

      case supplier => throw InvalidLineSupplier(supplier)
    }
  }
}
