package com.salsify.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.strategies.{RoundRobinShardsLinesDistribution, RoundRobinShardsLinesDistributionConfig}
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.common.config.HostConfig

import scala.concurrent.{ExecutionContext, Future}

class MockRoundRobinShardsLinesDistribution(numberOfShards: Int)(
  override implicit val materializer: ActorMaterializer,
  override implicit val system: ActorSystem,
  override implicit val executionContext: ExecutionContext
) extends RoundRobinShardsLinesDistribution(
  RoundRobinShardsLinesDistributionConfig(List(HostConfig("placeholder", 8080)))
) {
  require(numberOfShards > 0)

  override val shards: Seq[MockShardHttpClient] = (1 to numberOfShards)
    .map(index => new MockShardHttpClient(s"shard-$index", 8080))

  override def setup(lineSupplier: LinesInputSupplier): Future[Unit] = Future {
    lineSupplier.getLines().foreach { line =>
      setInt(line.number, line.content)
    }
  }
}
