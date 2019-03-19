package com.salsify.lineserver.client.distribution.strategies

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.{LinesDistribution, ShardHttpClient}
import com.salsify.lineserver.client.exception.{DistributionShardUploadException, DistributionException}
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Distributes lines evenly across a cluster of known shards.
  *
  * @param config           The configuration.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class RoundRobinShardsLinesDistribution(config: RoundRobinShardsLinesDistributionConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends LinesDistribution with LazyLogging {

  /**
    * The set of shards.
    */
  val shards: Seq[ShardHttpClient] = config.shards.map(config => new ShardHttpClient(config))

  /**
    * Returns the shard assigned to the line number provided as argument.
    *
    * @param lineNumber The line number.
    * @return The shard.
    */
  def shardFor(lineNumber: Int): ShardHttpClient = {
    assert(lineNumber > 0, "The line number > 0 invariant")
    shards(lineNumber % shards.length)
  }

  override def getInt(key: Int): Future[String] = shardFor(key).getInt(key)

  override def setInt(key: Int, value: String): Future[Unit] = shardFor(key).setInt(key, value)

  override def setup(lineSupplier: LinesInputSupplier): Future[Unit] = Future {
    logger.info(s"Setting up lines using $lineSupplier' and $this ...")

    val numberOfLines = lineSupplier.size
    def reportResult(currentNumProcessedLines: Int): Unit =
      if((currentNumProcessedLines % (numberOfLines / 10)) == 0) {
        val percentage = 1.0f * currentNumProcessedLines / numberOfLines
        logger.info(s"Processed $currentNumProcessedLines/$numberOfLines (${percentage * 100}%)")
      }

    val processedLines: AtomicReference[Int] = new AtomicReference[Int](0)
    val futures: Seq[Future[Unit]] = lineSupplier.getLines().map { line =>
        val targetShard = shardFor(line.index)
        val result = targetShard.setInt(line.index, line.content)

        result.onComplete {
          case Success(_)  => reportResult(processedLines.getAndUpdate(current => current + 1))
          case Failure(ex) => DistributionShardUploadException(targetShard.host, targetShard.port, line.index, line.content, ex)
        }

        result
    }

    Future.sequence(futures)
      .onComplete {
        case Success(_)   => logger.info(s"Processed ! ${processedLines.get()} lines! They are now available across ${shards.size} shards.")
        case Failure(ex)  => throw DistributionException(lineSupplier, this, ex)
      }
  }

  override def toString: String = s"RoundRobinShardsLinesDistribution(${config.shards})"
}
