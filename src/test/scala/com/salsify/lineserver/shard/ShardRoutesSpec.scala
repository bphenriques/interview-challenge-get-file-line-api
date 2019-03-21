package com.salsify.lineserver.shard

import akka.http.scaladsl.model.StatusCodes
import com.salsify.helpers.BaseSpec

/**
  * Tests for [[ShardRoutes]].
  */
class ShardRoutesSpec extends BaseSpec with ShardRoutes {

  override val handler: ShardResource = new ShardResource()

  it must "return HTTP 200 in healthcheck" in {
    Get("/health") ~> healthRoute() ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it must "return HTTP 404 if the key was not set" in {
    val rows = Table("Key", 0, 6)
    forAll (rows) { key: Int =>
      Get(s"/key/$key") ~> keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  it must "return HTTP 200 and the content" in {
    val rows = Table(
      ("Response", "Key"),
      ("Value 0", 0), //Key 0 should be accepted b/c it is a valid non-negative Long.
      ("Value 1", 1),
      ("Value 1", 1),
      ("Value 2", 2),
      ("Value 3", 3),
      ("Value 4", 4),
      ("Value 5", 5)
    )

    forAll (rows) { (value: String, key: Int) =>
      Put(s"/key/$key", value) ~> keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    forAll (rows) { (value: String, key: Int) =>
      Get(s"/key/$key") ~> keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual value
      }
    }
  }
}
