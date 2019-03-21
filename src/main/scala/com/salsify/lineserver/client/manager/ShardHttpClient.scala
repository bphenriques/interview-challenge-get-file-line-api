package com.salsify.lineserver.client.manager

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.exception.ShardHttpClientException
import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.shard.Shard

import scala.concurrent.{ExecutionContext, Future}

/**
  * Shard Http client.
  *
  * @param config           The configuration.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class ShardHttpClient(config: HostConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends Shard {

  /**
    * Http client.
    */
  private val http = Http()

  /**
    * Shard's host.
    */
  val host: String = config.host

  /**
    * Shards port.
    */
  val port: Int = config.port

  /**
    * @inheritdoc
    *
    * Fetches the value from the remote server using the Shard's REST Api.
    * <p>
    * Throws exception if the status is not HTTP 200 (ok).
    */
  override def getInt(key: Int): Future[String] =
    http.singleRequest(HttpRequest(uri = s"$host:$port/key/$key"))
      .map { entity =>
        entity.status match {
          case StatusCodes.OK => entity
          case code           => throw ShardHttpClientException(s"Unexpected HTTP $code in getInt($key).")
        }
      }
      .flatMap(response => Unmarshal(response).to[String])

  /**
    * @inheritdoc
    *
    * Sets the value using the Shard's REST Api.
    * <p>
    * Throws exception if the status is not HTTP 201 (created).
    */
  override def setInt(key: Int, value: String): Future[Unit] =
    Marshal(value).to[RequestEntity]
      .flatMap { entity =>
        val request = HttpRequest(method = HttpMethods.PUT, uri = s"$host:$port/key/$key", entity = entity)
        http.singleRequest(request)
      }
      .map { response => response.status match {
        case StatusCodes.Created =>
        case code                => throw ShardHttpClientException(s"Unexpected HTTP $code in setInt($key, _).")
      }
    }
}
