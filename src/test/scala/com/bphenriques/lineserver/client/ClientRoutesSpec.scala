/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.client

import akka.http.scaladsl.model.StatusCodes
import com.bphenriques.helpers.BaseSpec

/**
  * Tests [[ClientRoutes]].
  */
class ClientRoutesSpec extends BaseSpec {

  it must "return HTTP 200 in healthcheck" in {
    val route = new ClientRoutes(createCluster(3, Some(getResource("sample.txt"))))
    Get("/health") ~> route.healthRoute() ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it must "return HTTP 404 when the line is larger than the ones available" in {
    val rows = Table("Line Number", 0, 6)

    val route = new ClientRoutes(createCluster(3, Some(getResource("sample.txt"))))
    forAll (rows) { lineNumber: Int =>
      Get(s"/lines/$lineNumber") ~> route.linesRoutes() ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  it must "return HTTP 404 on line 1 with an empty file" in {
    val rows = Table("Line Number", 0, 1)

    val route = new ClientRoutes(createCluster(3, Some(getResource("empty.txt"))))
    forAll (rows) { lineNumber: Int =>
      Get(s"/lines/$lineNumber") ~> route.linesRoutes() ~> check {
        status shouldEqual StatusCodes.NotFound
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

    val route = new ClientRoutes(createCluster(3, Some(getResource("sample.txt"))))
    forAll (rows) { (line: String, lineNumber: Int) =>
      Get(s"/lines/$lineNumber") ~> route.linesRoutes() ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual line
      }
    }
  }
}
