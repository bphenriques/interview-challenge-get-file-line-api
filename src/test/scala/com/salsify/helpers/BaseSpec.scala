/*
 * © Copyright 2019 Bruno Henriques
 */

package com.salsify.helpers

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.{RouteTest, ScalatestRouteTest}
import com.salsify.lineserver.client.ClientResource
import com.salsify.lineserver.client.distribution.ShardHttpClient
import com.salsify.lineserver.client.input.strategies.{LocalFileLinesInputSupplier, LocalFileLinesInputSupplierConfig}
import com.salsify.lineserver.common.config.HostConfig
import com.salsify.lineserver.shard.ShardResource
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext

/**
  * Provides boiler plate code to streamline unit tests.
  */
class BaseSpec extends FlatSpec with Matchers with TableDrivenPropertyChecks with ScalaFutures with ScalatestRouteTest {

  val executionContext: ExecutionContext = executor

  val ValidHostConfig = HostConfig("localhost", 8080)

  val LocalhostConfig = HostConfig("localhost", 8080)

  val SampleLinesProviderConfig = LocalFileLinesInputSupplierConfig(getResource("sample.txt"))
  val SampleLinesProvider = LocalFileLinesInputSupplier.fromConfig(SampleLinesProviderConfig)

  val SampleLinesDistribution = new MockRoundRobinShardsLinesDistribution(3)
  SampleLinesDistribution.setup(SampleLinesProvider)

  val SampleClientResource = new ClientResource(
    SampleLinesProvider,
    SampleLinesDistribution
  )

  val SampleShardResource = new ShardResource()


  /**
    * Gets a resource file.
    *
    * @param file The name of the file.
    * @return The file.
    */
  def getResource(file: String): File = new File(getClass.getClassLoader.getResource(".").getPath, file)

  def createShardClient(host: String, port: Int = 8080): ShardHttpClient = new ShardHttpClient(HostConfig(host, port))
}
