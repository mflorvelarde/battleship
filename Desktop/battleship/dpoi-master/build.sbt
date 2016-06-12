name := """battleship"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

libraryDependencies += "org.json" % "json" % "20160212"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "signalJ" %% "signalj" % "0.5.0",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "jquery" % "2.1.1"
//   http://mvnrepository.com/artifact/org.avaje.ebeanorm/querybean-generator
//  "org.avaje.ebeanorm" % "querybean-generator" % "2.2.1",
//  "org.avaje.ebeanorm" % "avaje-ebeanorm-querybean" % "7.13.1"
)

lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)

routesGenerator := StaticRoutesGenerator

WebKeys.directWebModules in Assets += "signalj"

resolvers += "release repository" at "http://chanan.github.io/maven-repo/releases/"

resolvers += "snapshot repository" at "http://chanan.github.io/maven-repo/snapshots/"