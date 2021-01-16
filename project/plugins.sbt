// Linter
addSbtPlugin(
  "org.scalameta" % "sbt-scalafmt" % "2.4.0"
) // "2.4.0" is just sbt plugin version

// Native packager.
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.19")
