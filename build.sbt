val finchVersion     = "0.32.1"
val circeVersion     = "0.13.0"
val scalatestVersion = "3.2.3"
val catsCore         = "2.4.2"
val catsEffect       = "2.3.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.clockservice",
    name := "clock-service",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.4",
    scalacOptions ++= Seq(
      "-Ywarn-unused:imports",
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe" % finchVersion,
      "org.scalatest"      %% "scalatest"    % scalatestVersion % "test"
    ),
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.4.2"
  )
