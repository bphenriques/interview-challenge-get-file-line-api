package com.salsify.lineserver.client

import akka.actor.ActorSystem
import akka.http.caching.scaladsl.Cache
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directives, RequestContext, Route, RouteResult}
import com.salsify.lineserver.client.exception.LineNotFoundException

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ClientRoutes extends Directives {

  val handler: ClientResource

  implicit val system: ActorSystem
  implicit val executionContext: ExecutionContext

  val uriKeyer: PartialFunction[RequestContext, Uri] = {
    case r: RequestContext ⇒ r.request.uri
  }

  // Created outside the route to allow using
  // the same cache across multiple calls
  val lineCache: Cache[Uri, RouteResult] = routeCache[Uri]

  def linesRoutes(): Route =
    path("lines" / IntNumber) { lineIndex =>
      get {
//        alwaysCache(lineCache, uriKeyer) {
          handler.get(lineIndex) onComplete {
            case Success(line) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, line))
            case Failure(e) => e match {
              case LineNotFoundException(_) => complete(StatusCodes.RequestEntityTooLarge, e.getMessage)
              case _ => complete(StatusCodes.InternalServerError, s"An error occurred: ${e.getMessage}")
            }
          }

          onComplete(handler.get(lineIndex)) {
            case Success(line) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, line))
            case Failure(e) => e match {
              case LineNotFoundException(_) => complete(StatusCodes.RequestEntityTooLarge, e.getMessage)
              case _ => complete(StatusCodes.InternalServerError, s"An error occurred: ${e.getMessage}")
            }
          }
        //}
      }
    }

  def healthRoute(): Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }
}
