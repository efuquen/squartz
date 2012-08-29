parallelExecution in Test := false

resolvers += "Codahale Repo" at "http://repo.codahale.com"

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.1.3",
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "ch.qos.logback" % "logback-classic" % "1.0.1",
  "ch.qos.logback" % "logback-core" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.codahale" %% "jerkson" % "0.5.0",
  "org.scalatest" %% "scalatest" % "1.8" % "test"
)
