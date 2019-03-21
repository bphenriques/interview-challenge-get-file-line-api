package com.salsify.lineserver.client.manager.strategies

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.{ShardHttpClient, ShardsManager}
import com.salsify.lineserver.client.exception.{DistributionException, DistributionShardUploadException}
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.salsify.lineserver.client.manager.{ShardHttpClient, ShardsManager}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Distributes lines evenly across a cluster of known shards.
  * <p>
  * Invariant: The number of shards is always greater than 0.
  *
  * @param config           The configuration.
  * @param materializer     (implicit) The Akka actor materializer.
  * @param system           (implicit) The Akka actor system.
  * @param executionContext (implicit) The execution context.
  */
class RoundRobinShardsManager(config: RoundRobinShardManagerConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends ShardsManager with LazyLogging {

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

  /**
    * @inheritdoc
    *
    * Obtains the shard assigned to that line and gets the value.
    */
  override def getInt(key: Int): Future[String] = shardFor(key).getInt(key)

  /**
    * @inheritdoc
    *
    * Obtains the shard assigned to that line and sets the value. Moreover, it is idempotent.
    */
  override def setInt(key: Int, value: String): Future[Unit] = shardFor(key).setInt(key, value)

  /**
    * @inheritdoc
    *
    * Reads the files from the lineSupplier and distributes it across the available shards in round-robin using the
    * following formula: `shardId(line) = line % numberOfShards`.
    */
  override def setup(lineSupplier: LinesInputSupplier): Future[Unit] = Future {
    logger.info(s"Setting up lines using $lineSupplier' and $this ...")
    val numberOfLines = lineSupplier.size

    /**
      * Inner function to track the progress.
      *
      * @param currentNumProcessedLines The current number of processed lines.
      * @param frequency                The reporting frequency. Defaults to 10%.
      */
    def reportResult(currentNumProcessedLines: Int, frequency: Double = 0.10): Unit = {
      import com.salsify.lineserver.common.enrichers.DoubleEnricher._
      if ((currentNumProcessedLines % (numberOfLines * frequency)) ~= (0, precision = 1)) {
        val percentage = 1.0f * currentNumProcessedLines / numberOfLines
        logger.info(s"Processed $currentNumProcessedLines/$numberOfLines (${percentage * 100}%)")
      }
    }

    // Atomic counter to have a consistent progress counter.
    val processedLines: AtomicReference[Int] = new AtomicReference[Int](0)
    val linesInsertedFuture: Seq[Future[Unit]] = lineSupplier.getLines().map { line =>
      val targetShard = shardFor(line.index)
      val setOperation = targetShard.setInt(line.index, line.content)

      // Report if success and throw exception if not.
      setOperation.onComplete {
        case Success(_)  => reportResult(processedLines.getAndUpdate(current => current + 1))
        case Failure(ex) => DistributionShardUploadException(targetShard.host, targetShard.port, line.index, line.content, ex)
      }

      setOperation
    }

    // The sequence of every line being set. Reading the lines is streaming, as well
    Future.sequence(linesInsertedFuture)
      .onComplete {
        case Success(_)   => logger.info(s"Processed ! ${processedLines.get()} lines! They are now available across ${shards.size} shards.")
        case Failure(ex)  => throw DistributionException(lineSupplier, this, ex)
      }
  }

  override def toString: String = s"RoundRobinShardsLinesDistribution(${config.shards})"
}
