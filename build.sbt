lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "alpakka-ibm-mq-example",
    version := "0.1",
    scalaVersion := "2.12.6",
    organization := "com.example",
    scalafmtOnCompile in ThisBuild := true,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-jms" % "0.20",
      "com.ibm.mq" % "com.ibm.mq.allclient" % "9.0.4.0",
      "javax.resource" % "javax.resource-api" % "1.7.1",
      "org.specs2" %% "specs2-core" % "4.3.1" % "it,test"
    )
  )
