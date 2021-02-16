val finchVersion     = "0.32.1"
val circeVersion     = "0.11.1"
val scalatestVersion = "3.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.clockservice",
    name := "clock-service",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.13",
    scalacOptions ++= Seq(
      "-Ywarn-unused:imports",
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"   % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
      "io.circe"           %% "circe-generic" % circeVersion,
      "org.scalatest"      %% "scalatest"     % scalatestVersion % "test"
    )
  )
