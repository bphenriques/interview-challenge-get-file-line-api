import Dependencies._
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptExtraDefines

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / organization     := "com.salsify"
ThisBuild / name             := "lineserver"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organizationName := "bphenriques"
ThisBuild / maintainer       := "brunoaphenriques@gmail.com"

// Name of simulator configuration file.
lazy val ConfigurationFile = "application.conf"

lazy val root = (project in file("."))
  .settings(
    name := "lineserver",
    libraryDependencies ++= Seq(
      // Dependencies for the application.
      typeSafeConfig,
      scalaLogging,
      logbackClassic,

      // Dependencies wih Test scope.
      scalaTest % Test
    )
  )
  // Add packaging support.
  .enablePlugins(JavaAppPackaging)
  .settings(packagingSettings)


import NativePackagerHelper._
lazy val packagingSettings = Seq(
  // Set main class.
  mainClass in Compile := Some("com.salsify.lineserver.Main"),

  // Skip javadoc.jar build (https://github.com/sbt/sbt-native-packager/issues/651).
  mappings in (Compile, packageDoc) := Seq(),

  // Move all files under resource to conf.
  mappings in Universal ++= directory("src/main/resources")
    .map(t => (t._1, t._2.replace("resources", "conf"))),

  // Add LICENSE file.
  mappings in Universal += file("LICENSE") -> "LICENSE",

  // Add README.md.
  mappings in Universal += file("README.md") -> "README.md",

  // Add -Dconfig.file to use the configuration file in the conf folder.
  bashScriptExtraDefines += s"""addJava "-Dconfig.file=$${app_home}/../conf/$ConfigurationFile"""",

  // Add -Dlogback.configurationFile to use the logback.xml configuration file in the conf folder.
  bashScriptExtraDefines += s"""addJava "-Dlogback.configurationFile=$${app_home}/../conf/logback.xml""""
)
