/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.salsify.lineserver.common.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.ClientServer
import com.salsify.lineserver.client.config.ClientServerConfig
import com.salsify.lineserver.common.exception.LineServerConfigException
import com.salsify.lineserver.shard.ShardServer
import com.salsify.lineserver.shard.config.ShardServerConfig
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Factory of [[Server]].
  */
object ServerFactory {

  import com.salsify.lineserver.common.enrichers.ConfigEnricher._

  /**
    * Creates an instance of [[Server]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[Server]].
    */
  def from(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[Server] = Try {
    conf.getString("type") match {
      case "client" => conf.getConfig("client")
        .read[ClientServerConfig](ClientServerConfig.from)
        .map(c => new ClientServer(c))
        .get

      case "shard" => conf.getConfig("shard")
        .read[ShardServerConfig](ShardServerConfig.from)
        .map(c => new ShardServer(c))
        .get

      case server => throw LineServerConfigException(s"Unrecognized server type: '$server'")
    }
  }
}
