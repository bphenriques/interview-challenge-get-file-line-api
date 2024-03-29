/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.client.config

import akka.actor.ActorSystem
import com.bphenriques.lineserver.client.{ClientServer, config}
import com.bphenriques.lineserver.client.input.LinesInputSupplier
import com.bphenriques.lineserver.client.manager.{LinesManager, RoundRobinLinesManagerConfig}
import com.bphenriques.lineserver.common.config.ServerBindingConfig
import com.bphenriques.lineserver.client.input.LinesInputSupplierFactory
import com.bphenriques.lineserver.client.manager.RoundRobinLinesManager
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.Try

/** Configuration of [[ClientServer]]
  *
  * @param binding            The akka http binding.
  * @param linesSupplier      Optional. The lines supplier.
  * @param linesManager       The strategy used to manage the lines.
  */
final case class ClientServerConfig(
  binding: ServerBindingConfig,
  linesSupplier: Option[LinesInputSupplier],
  linesManager: LinesManager
)

/** Companion class of [[ClientServerConfig]].
  */
object ClientServerConfig {

  import com.bphenriques.lineserver.common.enrichers.ConfigEnricher._

  /** Creates an instance of [[ClientServerConfig]].
    *
    * @param conf             The configuration.
    * @param system           (implicit) The Akka actor system.
    * @param executionContext (implicit) The execution context.
    * @return An instance of [[ClientServerConfig]].
    */
  def from(conf: Config)(implicit
    system: ActorSystem,
    executionContext: ExecutionContext
  ): Try[ClientServerConfig] = for {
    httpConfig <- conf.getConfig("http").read[ServerBindingConfig](ServerBindingConfig.from)
    linesManagerConfig <- conf
      .getConfig("manager.shards-round-robin")
      .read[RoundRobinLinesManagerConfig](RoundRobinLinesManagerConfig.from)
  } yield config.ClientServerConfig(
    httpConfig,
    Try(LinesInputSupplierFactory.from(conf.getConfig("input"))).toOption.map(_.get),
    new RoundRobinLinesManager(linesManagerConfig)
  )
}
