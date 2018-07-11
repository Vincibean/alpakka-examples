lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "alpakka-rabbit-mq-example",
    version := "0.1",
    scalaVersion := "2.12.6",
    organization := "org.vincibean",
    scalafmtOnCompile in ThisBuild := true,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.20",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.13" % "it,test",
      "org.specs2" %% "specs2-core" % "4.3.1" % "it,test"
    )
  )
