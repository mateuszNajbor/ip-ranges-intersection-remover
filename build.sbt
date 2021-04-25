name := "IpRangesIntersectionRemover"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.0.0",
  "org.apache.spark" %% "spark-sql" % "3.0.0",
  "org.postgresql" % "postgresql" % "42.2.20",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.12.0",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.typesafe" % "config" % "1.4.1",
  "org.scalatest" %% "scalatest" % "3.2.7" % Test
)
