/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.client.manager

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy, QueueOfferResult}
import com.salsify.lineserver.client.exception.ShardHttpClientException
import com.salsify.lineserver.shard.Shard
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

/**
  * Shard Http client.
  *
  * @param config           The configuration.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class ShardHttpClient(config: ShardHttpClientConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends Shard with LazyLogging {

  /**
    * Shard's host.
    */
  val host: String = config.host

  /**
    * Shards port.
    */
  val port: Int = config.port

  /**
    * The connection pool to handle requests
    */
  private val connectionPool = Http().cachedHostConnectionPool[Promise[HttpResponse]](host.replace("http://", ""), port)

  /**
    * The size of internal queue to handle spikes.
    */
  private val QueueSize = config.queueSize

  /**
    * The connection queue using backpressure as the overflow strategy.
    */
  private val connectionQueue = Source
    .queue[(HttpRequest, Promise[HttpResponse])](QueueSize, OverflowStrategy.backpressure)
    .via(connectionPool)
    .toMat(
      Sink.foreach {
        case (Success(response), promise) => promise.success(response)
        case (Failure(e), promise)        => promise.failure(ShardHttpClientException(this, s"Error when executing request.", e))
      })(Keep.left)
    .run()

  /**
    * Adds request to the queue.
    *
    * @param request The HTTP request.
    * @return The future with the response.
    */
  private def enqueueRequest(request: HttpRequest): Future[HttpResponse] = {
    val promise = Promise[HttpResponse]
    connectionQueue.offer(request -> promise).flatMap {
      case QueueOfferResult.Enqueued    => promise.future
      case QueueOfferResult.Dropped     => throw ShardHttpClientException(this, s"Offer dropped when executing request $request.")
      case QueueOfferResult.Failure(ex) => throw ShardHttpClientException(this, s"Offer Failure when executing request $request.", ex)
      case QueueOfferResult.QueueClosed => throw ShardHttpClientException(this, s"Queue closed on $request.")
    }
  }

  /**
    * @inheritdoc
    *
    * Fetches the value from the remote server using the Shard's REST Api.
    * <p>
    * Throws exception if the status is not HTTP 200 (ok).
    */
  override def getString(key: Int): Future[String] =
    enqueueRequest(HttpRequest(uri = s"$host:$port/key/$key"))
      .map { response: HttpResponse =>
        response.status match {
          case StatusCodes.OK => response
          case code           => throw ShardHttpClientException(this, s"Unexpected HTTP $code in getString($key).")
        }
      }.flatMap(response => Unmarshal(response).to[String])

  /**
    * @inheritdoc
    *
    * Sets the value using the Shard's REST Api.
    * <p>
    * Throws exception if the status is not HTTP 201 (created).
    */
  override def setString(key: Int, value: String): Future[Unit] =
    enqueueRequest(HttpRequest(method = HttpMethods.PUT, uri = s"$host:$port/key/$key", entity = value))
      .map { response: HttpResponse =>
        response.status match {
          case StatusCodes.Created => response.discardEntityBytes() // Discard so that backpressure can kick in.
          case code                => throw ShardHttpClientException(this, s"Unexpected HTTP $code in setString($key, _).")
        }
      }

  /**
    * @inheritdoc
    */
  override def count(): Future[Int] =
    enqueueRequest(HttpRequest(uri = s"$host:$port/count"))
      .map { response =>
        response.status match {
          case StatusCodes.OK => response
          case code           => throw ShardHttpClientException(this, s"Unexpected HTTP $code in count().")
        }
      }
      .flatMap(response => Unmarshal(response).to[String])
      .map(_.toInt)
}
