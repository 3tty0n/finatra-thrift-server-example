import sbt.Keys._

parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra = "2.10.0"
  val guice = "4.0"
  val junit = "4.12"
  val logback = "1.1.7"
  val mockito = "1.9.5"
  val scalatest = "3.0.0"
  val scalacheck = "1.13.4"
  val specs2 = "2.4.17"
}

lazy val baseSettings = Seq(
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.11.11",
  ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true)),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest % "test"
  ),
  resolvers += Resolver.sonatypeRepo("releases"),
  fork in run := true
)

lazy val root = (project in file("."))
  .settings(
    name := "finatra-thrift-server-example",
    organization := "com.example",
    moduleName := "activator-thrift-seed",
    run := {
      (run in `server` in Compile).evaluated
    }
  )
  .aggregate(server)

lazy val server = (project in file("server"))
  .settings(baseSettings)
  .settings(
    name := "thrift-server",
    moduleName := "thrift-server",
    mainClass in (Compile, run) := Some("com.example.ExampleServerMain"),
    javaOptions ++= Seq(
      "-Dlog.service.output=/dev/stderr",
      "-Dlog.access.output=/dev/stderr"
    ),
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-thrift" % versions.finatra,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "com.twitter" %% "finatra-thrift" % versions.finatra % "test",
      "com.twitter" %% "inject-app" % versions.finatra % "test",
      "com.twitter" %% "inject-core" % versions.finatra % "test",
      "com.twitter" %% "inject-modules" % versions.finatra % "test",
      "com.twitter" %% "inject-server" % versions.finatra % "test",
      "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
      "com.twitter" %% "finatra-thrift" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests"
    )
  )
  .dependsOn(idl)

lazy val idl = (project in file("idl"))
  .settings(baseSettings)
  .settings(
    name := "thrift-idl",
    moduleName := "thrift-idl",
    scroogeThriftDependencies in Compile := Seq("finatra-thrift_2.11"),
    libraryDependencies ++= Seq(
      "org.apache.thrift" % "libthrift" % "0.9.2",
      "com.twitter" %% "scrooge-core" % "4.6.0" exclude("com.twitter", "libthrift"),
      "com.twitter" %% "finagle-thrift" % "6.34.0" exclude("com.twitter", "libthrift"),
      "com.twitter" %% "finatra-thrift" % versions.finatra exclude("com.twitter", "libthrift")
    )
  )

lazy val client = (project in file("client"))
  .settings(baseSettings)
  .settings(name := "thrift-client")
  .dependsOn(server)
