/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.server

import akka.actor.ActorSystem
import com.bphenriques.lineserver.client.ClientServer
import com.bphenriques.lineserver.client.config.ClientServerConfig
import com.bphenriques.lineserver.common.exception.LineServerConfigException
import com.bphenriques.lineserver.shard.ShardServer
import com.bphenriques.lineserver.shard.config.ShardServerConfig
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/** Factory of [[Server]].
  */
object ServerFactory {

  import com.bphenriques.lineserver.common.enrichers.ConfigEnricher._

  /** Creates an instance of [[Server]] given a [[Config]].
    *
    * @param conf             The configuration.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[Server]].
    */
  def from(
    conf: Config
  )(implicit system: ActorSystem, executionContext: ExecutionContext): Try[Server] =
    Try {
      conf.getString("type") match {
        case "client" =>
          conf
            .getConfig("client")
            .read[ClientServerConfig](ClientServerConfig.from)
            .map(c => new ClientServer(c))
            .get

        case "shard" =>
          conf
            .getConfig("shard")
            .read[ShardServerConfig](ShardServerConfig.from)
            .map(c => new ShardServer(c))
            .get

        case server => throw LineServerConfigException(s"Unrecognized server type: '$server'")
      }
    }
}
