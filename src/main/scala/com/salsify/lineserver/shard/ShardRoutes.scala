package com.salsify.lineserver.shard

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.salsify.lineserver.shard.exception.KeyNotFoundException
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

trait ShardRoutes extends Directives with LazyLogging {

  val handler: ShardResource

  def keyValueRoutes(): Route = path("key" / IntNumber) { key =>
    get {
      onComplete(handler.getInt(key)) {
        case Success(value) => complete(StatusCodes.OK -> value)
        case Failure(exception) => exception match {
          case KeyNotFoundException(_) => complete(StatusCodes.NotFound)
          case _ =>
            logger.error(s"Unrecognized error when obtaining value for key $key")
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~ put {
      entity(as[String]) { value =>
        onComplete(handler.setInt(key, value)) {
          case Success(_) => complete(StatusCodes.Created)
          case _ =>
            logger.error(s"Unrecognized error while setting key $key with value $value")
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }



  def healthRoute(): Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }
}
