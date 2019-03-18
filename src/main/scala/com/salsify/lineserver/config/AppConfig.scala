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

trait ServerConfig


/**
  * The Application configuration.
  */
final case class AppConfig(server: Server)

/**
  * Companion class of [[AppConfig]].
  */
object AppConfig {

  /**
    * Creates a [[AppConfig]].
    *
    * @param conf The configuration.
    * @return The instance of [[AppConfig]].
    */
  def fromConfig(conf: Config)(implicit
    materializer: ActorMaterializer,
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[AppConfig] = for {
    server <- ServerFactory.from(conf)
  } yield AppConfig(server)
}
