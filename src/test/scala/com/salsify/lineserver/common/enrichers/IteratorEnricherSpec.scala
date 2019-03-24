/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.common.enrichers

import com.salsify.helpers.BaseSpec

/**
  * Tests for [[IteratorEnricher]].
  */
class IteratorEnricherSpec extends BaseSpec {

  import com.salsify.lineserver.common.enrichers.IteratorEnricher._
  it must "Return the correct value" in {
    // Empty iterator.
    Iterator().zipWithIndex(0).size shouldEqual 0

    // Non-empty iterator.
    Iterator("A", "B").zipWithIndex(-5).toList shouldEqual List(("A", -5), ("B", -4))
  }
}
