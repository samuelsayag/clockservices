val finchVersion     = "0.32.1"
val circeVersion     = "0.13.0"
val scalatestVersion = "3.2.3"
val catsCore         = "2.4.2"
val catsEffect       = "2.3.1"

ThisBuild / organization := "com.clockservice"
ThisBuild / name := "clock-service"
ThisBuild / scalaVersion := "2.13.4"
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / scalacOptions ++= Seq(
  "-Ywarn-unused:imports",
  "-deprecation",
  "-encoding",
  "utf-8",
  "-feature",
  "-unchecked"
)
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val clockservice = (project in file("."))
  .settings(
    version := "0.0.1-SNAPSHOT",
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
