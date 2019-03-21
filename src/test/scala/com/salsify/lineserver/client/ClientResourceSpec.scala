package com.salsify.lineserver.client

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.exception.LineNotFoundException

/**
  * Tests [[ClientResourceSpec]].
  */
class ClientResourceSpec extends BaseSpec {

  it must "reject the line if outside of the range" in {
    val rows = Table("Line Number", -1, 0, 6)
    forAll (rows) { lineNumber: Int =>
      whenReady(SampleClientResource.get(lineNumber).failed) { e =>
        e shouldBe a[LineNotFoundException]
        e.asInstanceOf[LineNotFoundException].lineNumber shouldEqual lineNumber
      }
    }
  }

  it must "return the correct line" in {
    val rows = Table(
      ("Line", "Line Number"),
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    forAll (rows) { (line: String, lineNumber: Int) =>
      whenReady(SampleClientResource.get(lineNumber)) { result =>
        result shouldEqual line
      }
    }
  }

  it must "reject line 1 if the file is empty" in {
    whenReady(EmptyClientResource.get(1).failed) { e =>
      e shouldBe a[LineNotFoundException]
      e.asInstanceOf[LineNotFoundException].lineNumber shouldEqual 1
    }
  }
}
