/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.lineserver.shard

import akka.http.scaladsl.model.StatusCodes
import com.bphenriques.helpers.BaseSpec

/**
  * Tests for [[ShardRoutes]].
  */
class ShardRoutesSpec extends BaseSpec {

  it must "return HTTP 200 in healthcheck" in {
    val routes = new ShardRoutes()
    Get("/health") ~> routes.healthRoute() ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it must "return HTTP 404 if the key was not set" in {
    val rows = Table("Key", 0, 6)

    val routes = new ShardRoutes()
    forAll (rows) { key: Int =>
      Get(s"/key/$key") ~> routes.keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  it must "return HTTP 200 and the content" in {
    val rows = Table(
      ("Response", "Key"),
      ("Value 0", 0), //Key 0 should be accepted b/c it is a valid non-negative Integer.
      ("Value 1", 1),
      ("Value 1", 1),
      ("Value 2", 2),
      ("Value 3", 3),
      ("Value 4", 4),
      ("Value 5", 5)
    )

    val routes = new ShardRoutes()
    forAll (rows) { (value: String, key: Int) =>
      Put(s"/key/$key", value) ~> routes.keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    forAll (rows) { (value: String, key: Int) =>
      Get(s"/key/$key") ~> routes.keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual value
      }
    }
  }

  it must "return the correct count" in {
    val rows = Table(
      ("Response", "Key"),
      ("Value 0", 0), //Key 0 should be accepted b/c it is a valid non-negative Integer.
      ("Value 1", 1),
      ("Value 2", 2),
      ("Value 3", 3),
      ("Value 4", 4),
      ("Value 5", 5)
    )

    val routes = new ShardRoutes()
    // Check count before any key is inserted.
    Get("/count") ~> routes.countRoutes() ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual 0.toString
    }

    // Insert keys.
    forAll (rows) { (value: String, key: Int) =>
      Put(s"/key/$key", value) ~> routes.keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    // Check count after the keys were insert.
    Get("/count") ~> routes.countRoutes() ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual rows.size.toString
    }
  }
}
