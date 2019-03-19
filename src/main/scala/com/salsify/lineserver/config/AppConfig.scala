/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.lineserver.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.common.server.{Server, ServerFactory}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * The Application's configuration.
  *
  * @param server The server.
  */
final case class AppConfig(server: Server)

/**
  * Companion class of [[AppConfig]].
  */
object AppConfig {

  /**
    * Creates an instance of [[AppConfig]].
    *
    * @param conf             The configuration.
    * @param materializer     (implicit) The Akka actor materializer.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[AppConfig]].
    */
  def fromConfig(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[AppConfig] = for {
    server <- ServerFactory.from(conf)
  } yield AppConfig(server)
}
