/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.common.model

import com.salsify.helpers.BaseSpec

/**
  * Tests for [[Line]].
  */
class LineSpec extends BaseSpec {

  it must "reject lines with 0 or negative line number" in {
    val table = Table(
      ("Index", "Content"),
      (-1,      "Error due to negative line"),
      (0,       "Error due to line 0")
    )

    forAll(table) { (index: Int, content: String) =>
      assertThrows[Exception]{
        Line(index, content)
      }
    }
  }

  it must "instantiate correctly" in {
    val table = Table(
      ("Index", "Content"),
      (1,       "Line 1"), // Edge case.
      (2,       "")        // Empty line.
    )

    forAll(table) { (index: Int, content: String) =>
      val line = Line(index, content)
      line.index shouldEqual index
      line.content shouldEqual content
    }
  }
}
