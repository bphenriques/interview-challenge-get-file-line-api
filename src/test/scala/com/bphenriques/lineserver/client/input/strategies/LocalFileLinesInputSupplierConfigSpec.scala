/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.client.input.strategies

import java.io.File

import com.bphenriques.helpers.BaseSpec

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
