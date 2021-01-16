/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 *
 */

import sbt._

/**
  * Set of dependencies.
  */
object Dependencies {

  object Version {
    lazy val scalaTestVersion       = "3.2.2"
    lazy val typeSafeConfigVersion  = "1.4.1"
    lazy val logbackClassicVersion  = "1.2.3"
    lazy val scalaLogging           = "3.9.2"
    lazy val akkaHttpVersion        = "10.2.2"
    lazy val akkaVersion            = "2.6.11"
  }

  // Testing
  lazy val scalaTest            = "org.scalatest"               %% "scalatest"            % Version.scalaTestVersion
  lazy val akkaTest             = "com.typesafe.akka"           %% "akka-testkit"         % Version.akkaVersion
  lazy val akkaHttpTest         = "com.typesafe.akka"           %% "akka-http-testkit"    % Version.akkaHttpVersion

  // Configuration file
  lazy val typeSafeConfig       = "com.typesafe"                 % "config"               % Version.typeSafeConfigVersion

  // Logging
  lazy val logbackClassic       = "ch.qos.logback"               % "logback-classic"      % Version.logbackClassicVersion
  lazy val scalaLogging         = "com.typesafe.scala-logging"  %% "scala-logging"        % Version.scalaLogging

  // Server
  lazy val akkaHTTP             = "com.typesafe.akka"           %% "akka-http"            % Version.akkaHttpVersion
  lazy val akkaStream           = "com.typesafe.akka"           %% "akka-stream"          % Version.akkaVersion
  lazy val akkaCaching          = "com.typesafe.akka"           %% "akka-http-caching"    % Version.akkaHttpVersion
}
