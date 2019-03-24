/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.helpers

import java.io.File

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.salsify.lineserver.client.input.strategies.{LocalFileLinesInputSupplier, LocalFileLinesInputSupplierConfig}
import com.salsify.lineserver.client.manager.ShardHttpClient
import com.salsify.lineserver.shard.Shard
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Provides boiler plate code to streamline unit tests.
  */
class BaseSpec extends FlatSpec with Matchers with TableDrivenPropertyChecks with ScalaFutures with ScalatestRouteTest {

  /**
    * The execution context. Renaming because implicits require it.
    */
  val executionContext: ExecutionContext = executor

  /**
    * Creates a mock cluster.
    *
    * @param numberOfShards Number of shards.
    * @param file           Optional file to be served.
    * @return The mocked cluster.
    */
  def createCluster(numberOfShards: Int, file: Option[File] = None): MockRoundRobinLinesManager = {
    val cluster = new MockRoundRobinLinesManager(numberOfShards)

    if (file.isDefined) {
      val linesProvider = LocalFileLinesInputSupplier(LocalFileLinesInputSupplierConfig(file.get))
      val insert = linesProvider.getLines().map(l => cluster.setString(l.index, l.content))
      Await.result(Future.sequence(insert), Duration.Inf)
    }
    cluster
  }

  /**
    * Gets a resource file.
    *
    * @param file The name of the file.
    * @return The file.
    */
  def getResource(file: String): File = new File(getClass.getClassLoader.getResource(".").getPath, file)

  /**
    * Creates a mocked shard HTTP client.
    *
    * @param host The host.
    * @param port The port.
    * @return An instance of [[ShardHttpClient]].
    */
  def newMockShardClient(host: String, port: Int = 8080): Shard = new MockShardHttpClient(host, port)
}
