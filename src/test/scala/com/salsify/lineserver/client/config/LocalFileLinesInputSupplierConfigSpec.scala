package com.salsify.lineserver.client.config

import java.io.File

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.input.strategies.LocalFileLinesInputSupplierConfig

/**
  * Tests [[LocalFileLinesInputSupplierConfig]].
  */
class LocalFileLinesInputSupplierConfigSpec extends BaseSpec {

  it must "reject invalid arguments" in {
    val rows = Table(
      "File",
      new File(""), // Home folder.
      getResource(".file") // File that does not exist.
    )

    forAll (rows) { file: File =>
      assertThrows[Exception] {
        LocalFileLinesInputSupplierConfig(file)
      }
    }
  }

  it must "accept valid files" in {
    val file = getResource("sample.txt")
    LocalFileLinesInputSupplierConfig(file).file shouldEqual file
  }
}
