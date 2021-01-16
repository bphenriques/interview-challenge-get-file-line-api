/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.shard

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.bphenriques.lineserver.common.server.{RoutesProvider, Server}
import com.bphenriques.lineserver.shard.config.ShardServerConfig
import com.bphenriques.lineserver.common.server._

import scala.concurrent.ExecutionContext

/** Creates a Shard server.
  *
  * @param config             The configuration.
  * @param materializer       (implicit) The Akka actor materializer.
  * @param system             (implicit) The Akka actor system.
  * @param executionContext   (implicit) The execution context.
  */
final class ShardServer(config: ShardServerConfig)(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContext
) extends Server {

  override val host: String = config.binding.host

  override val port: Int = config.binding.port

  override protected def routesProvider(): RoutesProvider = new ShardRoutes()
}
