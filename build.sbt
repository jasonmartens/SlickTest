
name := "SlickTest"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.h2database" % "h2" % "1.4.187" % "test",
  "org.scalatest" %% "scalatest" %"3.0.1"
)