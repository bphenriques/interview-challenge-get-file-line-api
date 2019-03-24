/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client

import com.salsify.helpers.BaseSpec
import com.salsify.lineserver.client.exception.LineNotFoundException

/**
  * Tests [[ClientResourceSpec]].
  */
class ClientResourceSpec extends BaseSpec {

  it must "reject the line if outside of the range" in {
    val rows = Table("Line Number", -1, 0, 6)

    val sampleClientResource = new ClientResource(createCluster(3, Some(getResource("sample.txt"))))
    forAll (rows) { lineNumber: Int =>
      whenReady(sampleClientResource.get(lineNumber).failed) { e =>
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

    val sampleClientResource = new ClientResource(createCluster(3, Some(getResource("sample.txt"))))
    forAll (rows) { (line: String, lineNumber: Int) =>
      whenReady(sampleClientResource.get(lineNumber)) { result =>
        result shouldEqual line
      }
    }
  }

  it must "reject line 1 if the file is empty" in {
    val sampleClientResource = new ClientResource(createCluster(3))
    whenReady(sampleClientResource.get(1).failed) { e =>
      e shouldBe a[LineNotFoundException]
      e.asInstanceOf[LineNotFoundException].lineNumber shouldEqual 1
    }
  }
}
