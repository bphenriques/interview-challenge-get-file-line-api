package com.salsify.lineserver.client.input.strategies

import java.io.File

import com.salsify.lineserver.client.input.LinesInputSupplierConfig
import com.typesafe.config.Config

import scala.util.Try

case class LocalFileLinesInputSupplierConfig(file: File) extends LinesInputSupplierConfig {
  require(file.exists && file.isFile, "TODO")
}

object LocalFileLinesInputSupplierConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  def fromConfig(conf: Config): Try[LocalFileLinesInputSupplierConfig] = for {
    file <- Try(conf.readFile("path"))
  } yield LocalFileLinesInputSupplierConfig(file)
}
