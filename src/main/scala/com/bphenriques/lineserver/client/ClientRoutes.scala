/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client

import akka.actor.ActorSystem
import akka.http.caching.scaladsl.Cache
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directives, RequestContext, Route, RouteResult}
import com.bphenriques.lineserver.client.exception.LineNotFoundException
import com.bphenriques.lineserver.client.manager.LinesManager
import com.bphenriques.lineserver.common.server.RoutesProvider
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/** Main Akka routes that publicly makes available the lines.
  *
  * @param linesManager       The lines manager.
  * @param system             (implicit) The Akka actor system.
  * @param executionContext   (implicit) The execution context.
  */
final class ClientRoutes(linesManager: LinesManager)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends RoutesProvider
    with Directives
    with LazyLogging {

  override def routes(): Route = healthRoute() ~ linesRoutes()

  /** The routes handler.
    */
  private val handler: ClientResource = new ClientResource(linesManager)

  /** The cache key: The full uri.
    */
  private val uriKeyer: PartialFunction[RequestContext, Uri] = { case r: RequestContext ⇒
    r.request.uri
  }

  /** The lines cache.
    */
  private val lineCache: Cache[Uri, RouteResult] = routeCache[Uri]

  /** The `/lines` endpoints.
    * <ul>
    *   <li>`GET /lines/<line-index>`: Returns the line at `<line-index>` with HTTP 200. Returns HTTP 413 if out of range.</li>
    * </ul>
    *
    * @return The `/lines` endpoints.
    */
  def linesRoutes(): Route =
    path("lines" / IntNumber) { lineIndex =>
      get {
        alwaysCache(lineCache, uriKeyer) {
          onComplete(handler.get(lineIndex)) {
            case Success(line) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, line))
            case Failure(exception) =>
              exception match {
                case LineNotFoundException(_) => complete(StatusCodes.NotFound, exception.getMessage)
                case e =>
                  logger.error(s"Unrecognized error while getting line $lineIndex", e)
                  complete(StatusCodes.InternalServerError, s"An error occurred: ${e.getMessage}")
              }
          }
        }
      }
    }

  /** The `/health` endpoint. Always returns HTTP 200.
    * @return The `/health` endpoint.
    */
  def healthRoute(): Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }
}
