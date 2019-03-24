/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.salsify.lineserver.client

import akka.http.scaladsl.model.StatusCodes
import com.salsify.helpers.BaseSpec

/**
  * Tests [[ClientRoutes]].
  */
class ClientRoutesSpec extends BaseSpec with ClientRoutes {

  override def createHandler(): ClientResource = new ClientResource(createCluster(3, Some(getResource("sample.txt"))))

  it must "return HTTP 200 in healthcheck" in {
    Get("/health") ~> healthRoute() ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it must "return HTTP 413 with lines out of range" in {
    val rows = Table("Line Number", 0, 6)
    forAll (rows) { lineNumber: Int =>
      Get(s"/lines/$lineNumber") ~> linesRoutes() ~> check {
        status shouldEqual StatusCodes.RequestEntityTooLarge
      }
    }
  }

  it must "return HTTP 200 and the line" in {
    val rows = Table(
      ("Line", "Line Number"),
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    forAll (rows) { (line: String, lineNumber: Int) =>
      Get(s"/lines/$lineNumber") ~> linesRoutes() ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual line
      }
    }
  }
}
