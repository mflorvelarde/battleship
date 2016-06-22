name := """battleship"""

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayJava)
lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)


scalaVersion := "2.11.1"

//libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"
//
//libraryDependencies += "org.json" % "json" % "20160212"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "jquery" % "2.1.1",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.json" % "json" % "20160212",
  "com.typesafe.akka" %% "akka-actor" % "2.4.7"
)

//routesGenerator := StaticRoutesGenerator
//routesGenerator := InjectedRoutesGenerator


