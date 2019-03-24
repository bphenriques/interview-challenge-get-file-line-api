/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.shard

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.salsify.lineserver.shard.exception.KeyNotFoundException
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

/**
  * Akka routes that makes available the key-value store.
  */
trait ShardRoutes extends Directives with LazyLogging {

  /**
    * The routes handler.
    */
  val handler: ShardResource

  /**
    * The `/key` endpoints.
    * <ul>
    *   <li>`GET /key/<index>`: Returns the String value at `<index>` with HTTP 200. Returns HTTP 404 if not found.</li>
    *   <li>`SET /key/<index>`: Sets the body's content as the String value of `<index>`. Returns HTTP 200 if successful.</li>
    * </ul>
    *
    * @return The `/key` endpoints.
    */
  def keyValueRoutes(): Route = path("key" / IntNumber) { key =>
    get {
      onComplete(handler.getString(key)) {
        case Success(value) => complete(StatusCodes.OK -> value)
        case Failure(exception) => exception match {
          case KeyNotFoundException(_) => complete(StatusCodes.NotFound)
          case e =>
            logger.error(s"Unrecognized error when obtaining value for key $key", e)
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~ put {
      entity(as[String]) { value =>
        onComplete(handler.setString(key, value)) {
          case Success(_) => complete(StatusCodes.Created)
          case e =>
            logger.error(s"Unrecognized error while setting key $key with value $value", e)
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  /**
    * The `/count` endpoint.
    * <ul>
    *   <li>`GET /count`: Returns the number of stored keys with HTTP 200.</li>
    * </ul>
    *
    * @return The `/key` endpoints.
    */
  def countRoutes(): Route = path("count") {
    get {
      onComplete(handler.count()) {
        case Success(size) => complete(StatusCodes.OK -> size.toString)
        case e =>
          logger.error(s"Unrecognized error while obtaining count", e)
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  /**
    * The `/health` endpoint. Always returns HTTP 200.
    * @return The `/health` endpoint.
    */
  def healthRoute(): Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }
}
