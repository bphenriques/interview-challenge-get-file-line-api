package com.salsify.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.manager.strategies.RoundRobinShardManagerConfig
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.client.manager.strategies.{RoundRobinShardManagerConfig, RoundRobinShardsManager}
import com.salsify.lineserver.common.config.HostConfig

import scala.concurrent.{ExecutionContext, Future}

/**
  * Simulates an in-memory cluster using [[MockShardHttpClient]].
  *
  * @param numberOfShards   The number of shards.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class MockRoundRobinShardsManager(numberOfShards: Int)(
  override implicit val materializer: ActorMaterializer,
  override implicit val system: ActorSystem,
  override implicit val executionContext: ExecutionContext
) extends RoundRobinShardsManager(
  RoundRobinShardManagerConfig(List(HostConfig("placeholder", 8080)))
) {
  require(numberOfShards > 0)

  /**
    * Creates an in-memory sequence of mocked shard clients.
    */
  override val shards: Seq[MockShardHttpClient] =
    (1 to numberOfShards)
      .map(index => new MockShardHttpClient(s"shard-$index", 8080))

  /**
    * Reads the file and delegates file distribution to super.
    *
    * @param lineSupplier The line supplier.
    * @return The future when the setup has completed.
    */
  override def setup(lineSupplier: LinesInputSupplier): Future[Unit] = Future {
    lineSupplier.getLines().foreach { line =>
      setInt(line.index, line.content)
    }
  }
}
