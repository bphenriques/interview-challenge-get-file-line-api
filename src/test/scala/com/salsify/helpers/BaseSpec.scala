/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.helpers

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

/**
  * Provides boiler plate code to streamline unit tests.
  */
class BaseSpec extends FlatSpec with Matchers {

  /**
    * Gets a resource file.
    *
    * @param file The name of the file.
    * @return The file.
    */
  def getValidResource(file: String): File = new File(getClass.getClassLoader.getResource(file).getFile)
}
