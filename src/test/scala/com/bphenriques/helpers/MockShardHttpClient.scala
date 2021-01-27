/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.helpers

import akka.actor.ActorSystem
import com.bphenriques.lineserver.shard.ShardResource
import com.bphenriques.lineserver.shard.Shard

import scala.concurrent.{ExecutionContext, Future}

/**
  * Mocks a HTTP client by using an in-memory [[ShardResource]] that is identical type used by the Shard's server.
  *
  * @param host             The host.
  * @param port             The port.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class MockShardHttpClient(host: String, port: Int)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends Shard {

  /**
    * The shard's route handler.
    */
  private val resource = new ShardResource()

  override def getString(key: Int): Future[String] = resource.getString(key)

  override def setString(key: Int, value: String): Future[Unit] = resource.setString(key, value)

  override def count(): Future[Int] = resource.count()
}
