package com.salsify.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.ShardHttpClient
import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.shard.ShardResource

import scala.concurrent.{ExecutionContext, Future}


class MockShardHttpClient(host: String, port: Int)(
  override implicit val materializer: ActorMaterializer,
  override implicit val system: ActorSystem,
  override implicit val executionContext: ExecutionContext
) extends ShardHttpClient(HostConfig(host, port))(materializer, system, executionContext) {

  val resource = new ShardResource()

  override def getInt(key: Int): Future[String] = resource.getInt(key)

  override def setInt(key: Int, value: String): Future[Unit] = resource.setInt(key, value)
}
