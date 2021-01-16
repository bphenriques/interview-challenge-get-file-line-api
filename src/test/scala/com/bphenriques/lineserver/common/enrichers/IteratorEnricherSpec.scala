/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.common.enrichers

import com.bphenriques.helpers.BaseSpec

/**
  * Tests for [[IteratorEnricher]].
  */
class IteratorEnricherSpec extends BaseSpec {

  import IteratorEnricher._
  it must "Return the correct value" in {
    // Empty iterator.
    Iterator().zipWithIndex(0).size shouldEqual 0

    // Non-empty iterator.
    Iterator("A", "B").zipWithIndex(-5).toList shouldEqual List(("A", -5), ("B", -4))
  }
}
