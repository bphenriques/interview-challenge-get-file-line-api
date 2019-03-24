/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.helpers

import java.io.File

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.salsify.lineserver.client.ClientResource
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
    * Lines Provider given a sample file with few lines.
    */
  val SampleLinesProvider = LocalFileLinesInputSupplier(LocalFileLinesInputSupplierConfig(getResource("sample.txt")))

  /**
    * Cluster that serves the sample file.
    */
  val SampleCluster: MockRoundRobinLinesManager = {
    val cluster = new MockRoundRobinLinesManager(3)
    val insert = SampleLinesProvider.getLines().map(l => cluster.setString(l.index, l.content))
    Await.result(Future.sequence(insert), Duration.Inf)
    cluster
  }

  /**
    * Routes resource that serves the sample file.
    */
  val SampleClientResource = new ClientResource(SampleCluster)

  /**
    * Routes resource that serves the empty file.
    */
  val EmptyClientResource = new ClientResource(new MockRoundRobinLinesManager(3))

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
