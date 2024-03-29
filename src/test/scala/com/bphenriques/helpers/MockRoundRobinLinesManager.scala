/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

package com.bphenriques.helpers

import akka.actor.ActorSystem
import com.bphenriques.lineserver.client.manager.LinesManager
import com.bphenriques.lineserver.shard.Shard

import scala.concurrent.{ExecutionContext, Future}

/**
  * Simulates an in-memory cluster using [[MockShardHttpClient]].
  *
  * @param numberOfShards   The number of shards.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class MockRoundRobinLinesManager(numberOfShards: Int)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends LinesManager {
  require(numberOfShards > 0)

  /**
    * Returns the shard assigned to the line number provided as argument.
    *
    * @param lineNumber The line number.
    * @return The shard.
    */
  private def shardFor(lineNumber: Int): Shard = {
    assert(lineNumber > 0, "The line number > 0 invariant")
    shards(lineNumber % shards.length)
  }

  /**
    * Creates an in-memory sequence of mocked shard clients.
    */
  val shards: Seq[Shard] =
    (1 to numberOfShards).map(index => new MockShardHttpClient(s"shard-$index", 8080))

  override def getString(key: Int): Future[String] = shardFor(key).getString(key)

  override def setString(key: Int, value: String): Future[Unit] = shardFor(key).setString(key, value)

  override def count(): Future[Int] = Future.sequence(shards.map(_.count())).map(counts => counts.sum)
}
