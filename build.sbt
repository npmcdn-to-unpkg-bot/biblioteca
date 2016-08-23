name := """biblioteca"""

version := "1.0-SNAPSHOT"

import com.github.play2war.plugin._

//WAR plugins
Play2WarPlugin.play2WarSettings

//Servlet 3.1: Tomcat 8, Wildfly 8, Glassfish 4, Jetty 9, ...
Play2WarKeys.servletVersion := "3.1"

//Servlet 3.0: Tomcat 7, JBoss 7, JBoss EAP 6, Glassfish 3, Jetty 8, ...
//Play2WarKeys.servletVersion := "3.0"

//Servlet 2.5: Tomcat 6, JBoss AS 5/6, JBoss EAP 5, Glassfish 2, Jetty 7, ...
//Play2WarKeys.servletVersion := "2.5"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"


libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  //"org.postgresql" % "postgresql" % "9.4-1203-jdbc42",
  "com.typesafe.play" %% "play-mailer" % "4.0.0-M1",
  "org.julienrf" %% "play-jsmessages" % "2.0.0",
  "org.webjars.bower" % "angularjs" % "1.5.8",
  "org.webjars" % "requirejs" % "2.1.11-1",
  //"io.swagger" %% "swagger-play2" % "1.5.1",
  "org.webjars" %% "webjars-play" % "2.3.0-2"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
