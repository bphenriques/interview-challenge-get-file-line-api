package com.salsify.lineserver.shard

import akka.http.scaladsl.model.StatusCodes
import com.salsify.helpers.BaseSpec

class ShardRoutesSpec extends BaseSpec with ShardRoutes {

  override val handler: ShardResource = SampleShardResource

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
      ("Line 1", 1),
      ("Line 2", 2),
      ("Line 3", 3),
      ("Line 4", 4),
      ("Line 5", 5)
    )

    forAll (rows) { (content: String, key: Int) =>
      Put(s"/key/$key", content) ~> keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    forAll (rows) { (content: String, key: Int) =>
      Get(s"/key/$key") ~> keyValueRoutes() ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual content
      }
    }
  }
}
