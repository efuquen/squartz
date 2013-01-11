name := "squartz"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0")

organization := "org.squartz"

parallelExecution in Test := false

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("http://squartz.org"))

resolvers ++= Seq(
  "Codahale Repo" at "http://repo.codahale.com",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.1.3",
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "ch.qos.logback" % "logback-classic" % "1.0.1",
  "ch.qos.logback" % "logback-core" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "net.liftweb" %% "lift-json" % "2.5-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <scm>
    <url>git@github.com:efuquen/squartz.git</url>
    <connection>scm:git:git@github.com:efuquen/squartz.git</connection>
  </scm>
  <developers>
    <developer>
      <id>efuquen</id>
      <name>Edwin Fuquen</name>
      <url>http://edftwin.com</url>
    </developer>
  </developers>)
