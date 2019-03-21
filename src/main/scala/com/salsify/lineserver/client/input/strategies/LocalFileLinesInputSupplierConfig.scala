package com.salsify.lineserver.client.input.strategies

import java.io.File

import com.salsify.lineserver.client.input.LinesInputSupplierConfig
import com.typesafe.config.Config

import scala.util.Try

/**
  * Configuration of [[LocalFileLinesInputSupplier]].
  *
  * @param file The file. It must exist and be a file.
  */
final case class LocalFileLinesInputSupplierConfig(file: File) extends LinesInputSupplierConfig {
  require(file.exists && file.isFile, s"The file ${file.getAbsolutePath} must exist and be a file.")
}

/**
  * Companion object of [[LocalFileLinesInputSupplierConfig]].
  */
object LocalFileLinesInputSupplierConfig {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[LocalFileLinesInputSupplierConfig]].
    *
    * @param conf The configuration.
    * @return An instance of [[LocalFileLinesInputSupplierConfig]].
    */
  def from(conf: Config): Try[LocalFileLinesInputSupplierConfig] = for {
    file <- Try(conf.readFile("path"))
  } yield LocalFileLinesInputSupplierConfig(file)
}
