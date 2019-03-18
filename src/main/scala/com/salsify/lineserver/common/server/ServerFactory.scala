package com.salsify.lineserver.common.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.ClientServer
import com.salsify.lineserver.shard.config.ShardServerConfig
import com.salsify.lineserver.config.AppConfig
import com.salsify.lineserver.client.config.ClientServerConfig
import com.salsify.lineserver.shard.ShardServer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

object ServerFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[Server] = Try {
    conf.getString("type") match {
      case "client" => conf.getConfig("client")
        .read[ClientServerConfig](ClientServerConfig.fromConfig)
        .map(c => new ClientServer(c))
        .get
      case "shard" => conf.getConfig("shard")
        .read[ShardServerConfig](ShardServerConfig.fromConfig)
        .map(c => new ShardServer(c))
        .get
      case server => throw new InternalError(s"Unrecognized server type: '$server'")
    }
  }
}
