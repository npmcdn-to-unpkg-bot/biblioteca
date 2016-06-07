name := """architectplay"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1203-jdbc42",
  "com.typesafe.play" %% "play-mailer" % "4.0.0-M1",
  "org.julienrf" %% "play-jsmessages" % "2.0.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
