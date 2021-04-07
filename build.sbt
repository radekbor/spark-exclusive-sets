name := "ip-ranges"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies := Seq(
  // ip addr
  "com.risksense" % "ipaddr_2.12" % Versions.ipaddr,
  // spark
  "org.apache.spark" %% "spark-core" % Versions.spark,
  "org.apache.spark" %% "spark-sql" % Versions.spark,
  // scala test
  "org.scalactic" %% "scalactic" % Versions.scalaTest,
  "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
)
