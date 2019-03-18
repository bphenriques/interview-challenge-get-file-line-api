package com.salsify.lineserver.client.input.strategies

import java.io.File

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.common.model.Line

/**
  * Tests [[LocalFileLinesInputSupplier]].
  */
class LocalFileLinesInputSupplierSpec extends BaseSpec {

  it must "return the correct lines and contents" in {
    val rows = Table(
      ("File", "Expected Lines"),
      (getResource("empty.txt"), Seq()),
      (getResource("empty-line.txt"), Seq(Line(1, ""))),
      (getResource("sample.txt"), Seq(Line(1, "Line 1"), Line(2, "Line 2"), Line(3, "Line 3"), Line(4, "Line 4"), Line(5, "Line 5")))
    )

    forAll (rows) { (file: File, expectedLines: Seq[Line]) =>
      val supplier = LocalFileLinesInputSupplier.fromConfig(LocalFileLinesInputSupplierConfig(file))
      supplier.size shouldEqual expectedLines.size

      val lines = supplier.getLines()
      lines.size shouldEqual lines.size
      lines shouldEqual expectedLines
    }
  }
}
