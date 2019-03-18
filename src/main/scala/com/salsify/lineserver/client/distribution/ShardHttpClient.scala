package com.salsify.lineserver.client.distribution

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
import scala.util.{Failure, Success}

class ShardHttpClient(config: HostConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends Shard {

  private val http = Http()

  val host: String = config.host
  val port: Int = config.port

  override def getInt(key: Int): Future[String] = {
    val request = HttpRequest(uri = s"$host:$port/key/$key")

    http.singleRequest(request)
      .map { entity =>
        entity.status match {
          case StatusCodes.OK => entity
          case code        => throw ShardHttpClientException(s"Unexpected HTTP $code in getInt($key).")
        }
      }.onComplete {
      case Success(entity) => Unmarshal(entity).to[String]
      case Failure(e)      => throw e
    }

    for {
      response <- http.singleRequest(request)
      line <- Unmarshal(response.entity).to[String]
    } yield line
  }

  override def setInt(key: Int, value: String): Future[Unit] = Future {
    Marshal(value).to[RequestEntity].flatMap { entity =>
      val request = HttpRequest(method = HttpMethods.PUT, uri = s"$host:$port/key/$key", entity = entity)

      http.singleRequest(request)
    }.onComplete {
      case Failure(error) => throw error
      case Success(httpResponse) => httpResponse.status match {
        case StatusCodes.Created =>
        case code                => throw ShardHttpClientException(s"Unexpected HTTP $code in setInt($key, _).")
      }
    }
  }

  override def equals(client: Any): Boolean = {
    client match {
      case other: ShardHttpClient => config.host == other.host && config.port == other.port
      case _ => false
    }
  }

  override def hashCode: Int = config.hashCode()
}
