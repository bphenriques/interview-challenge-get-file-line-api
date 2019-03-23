package com.salsify.lineserver.client.manager.strategies

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.salsify.lineserver.client.manager.{LinesManager, ShardHttpClient}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

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
class RoundRobinLinesManager(config: RoundRobinLinesManagerConfig)(
  implicit val materializer: ActorMaterializer,
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContext
) extends LinesManager with LazyLogging {

  /**
    * The set of shards.
    */
  private val shards: Seq[ShardHttpClient] = config.shards.map(config => new ShardHttpClient(config))

  /**
    * Returns the shard assigned to the line number provided as argument.
    *
    * @param lineNumber The line number.
    * @return The shard.
    */
  private def shardFor(lineNumber: Int): ShardHttpClient = {
    assert(lineNumber > 0, "The line number > 0 invariant")
    shards(lineNumber % shards.length)
  }

  /**
    * @inheritdoc
    *
    * Obtains the shard assigned to that line and gets the value.
    */
  override def getString(key: Int): Future[String] = shardFor(key).getString(key)

  /**
    * @inheritdoc
    *
    * Obtains the shard assigned to that line and sets the value. Moreover, it is idempotent.
    */
  override def setString(key: Int, value: String): Future[Unit] = shardFor(key).setString(key, value)

  /**
    * @inheritdoc
    */
  override def count(): Future[Int] = Future.sequence(shards.map(_.count())).map(counts => counts.sum)

  override def toString: String = s"${getClass.getSimpleName}(${config.shards})"
}
