name := "reactive-board"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.akka" %% "akka-remote" % "2.2.0"
)     

play.Project.playScalaSettings
