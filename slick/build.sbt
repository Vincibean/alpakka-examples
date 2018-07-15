lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "alpakka-slick-example",
    version := "0.1",
    scalaVersion := "2.12.6",
    organization := "com.example",
    scalafmtOnCompile in ThisBuild := true,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.20",
      "com.h2database" % "h2" % "1.4.197",
      "org.specs2" %% "specs2-core" % "4.3.1" % "it,test"
    )
  )
