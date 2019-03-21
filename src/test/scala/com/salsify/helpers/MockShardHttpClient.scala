package com.salsify.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.manager.ShardHttpClient
import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.shard.ShardResource

import scala.concurrent.{ExecutionContext, Future}

/**
  * Mocks a HTTP client by using an in-memory [[ShardResource]] that is identical type used by the Shard's rest API.
  *
  * @param host             The host.
  * @param port             The port.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class MockShardHttpClient(host: String, port: Int)(
  override implicit val materializer: ActorMaterializer,
  override implicit val system: ActorSystem,
  override implicit val executionContext: ExecutionContext
) extends ShardHttpClient(HostConfig(host, port))(materializer, system, executionContext) {

  /**
    * The shard's route handler.
    */
  private val resource = new ShardResource()

  override def getInt(key: Int): Future[String] = resource.getInt(key)

  override def setInt(key: Int, value: String): Future[Unit] = resource.setInt(key, value)
}
