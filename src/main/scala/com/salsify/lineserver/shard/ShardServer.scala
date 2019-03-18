package com.salsify.lineserver.shard

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.salsify.lineserver.common.server._
import com.salsify.lineserver.shard.config.ShardServerConfig

import scala.concurrent.ExecutionContext

class ShardServer(config: ShardServerConfig)(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContext,
) extends Server with ShardRoutes {

  override val host: String = config.binding.host
  override val port: Int = config.binding.port

  override val routes: Route = keyValueRoutes() ~ healthRoute()
  override val handler = new ShardResource()
}
