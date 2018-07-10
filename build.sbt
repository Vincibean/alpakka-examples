name := "alpakka-ibm-mq"

version := "0.1"

scalaVersion := "2.12.6"

scalafmtOnCompile in ThisBuild := true

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value
