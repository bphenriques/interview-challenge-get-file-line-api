package com.salsify.lineserver.client.distribution.strategies

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.distribution.{LinesDistribution, ShardHttpClient}
import com.salsify.lineserver.client.exception.{ShardUploadException, UploadException}
import com.salsify.lineserver.client.input.LinesInputSupplier
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RoundRobinShardsLinesDistribution(config: RoundRobinShardsLinesDistributionConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends LinesDistribution with LazyLogging {

  override def description: String = s"RoundRobinShardsLinesDistribution(${config.shards})"

  val shards: Seq[ShardHttpClient] = config.shards.map(config => new ShardHttpClient(config))

  /**
    * Document that this is safe cast to Int
    * @param lineNumber
    * @return
    */
  def shardFor(lineNumber: Int): ShardHttpClient = {
    assert(lineNumber > 0, "The line number > 0 invariant")
    shards(lineNumber % shards.length)
  }

  override def getInt(key: Int): Future[String] = shardFor(key).getInt(key)

  override def setInt(key: Int, value: String): Future[Unit] = shardFor(key).setInt(key, value)

  override def setup(lineSupplier: LinesInputSupplier): Future[Unit] = Future {
    logger.info(s"Setting up lines using ${lineSupplier.description}' and $description ...")

    val numberOfLines = lineSupplier.size

    val processedLines: AtomicReference[Int] = new AtomicReference[Int](0)
    def reportResult(currentNumProcessedLines: Int): Unit =
      if((currentNumProcessedLines % (numberOfLines / 10)) == 0) {
        val percentage = 1.0f * currentNumProcessedLines / numberOfLines
        logger.info(s"Processed $currentNumProcessedLines/$numberOfLines (${percentage * 100}%)")
      }

    val futures: Seq[Future[Unit]] = lineSupplier.getLines().map { line =>
        val targetShard = shardFor(line.number)
        val result = targetShard.setInt(line.number, line.content)

        result.onComplete {
          case Success(_)  => reportResult(processedLines.updateAndGet(current => current + 1))
          case Failure(ex) => ShardUploadException(targetShard.host, targetShard.port, line.number, line.content, ex)
        }

        result
    }

    Future.sequence(futures)
      .onComplete {
        case Success(_)   => logger.info(s"Success! ${processedLines.get()} lines available across ${shards.size} shards.")
        case Failure(ex)  => throw UploadException(lineSupplier, ex)
      }
  }
}
