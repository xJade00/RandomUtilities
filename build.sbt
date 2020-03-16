name := "Random Utilities"
description := "A bunch of random utilities."
//autoScalaLibrary := false // We don't want people using this to auto have the java SDK
scalaVersion := "2.13.1"

inThisBuild(
  Seq(
    version := "1.0.0",
    developers := List(
      Developer(id = "xaanit",
                name = "Jacob Frazier",
                email = "shadowjacob1@gmail.com",
                url = new URL("https://www.xaan.it"))
    ),
    startYear := Some(2020),
    homepage := Some(new URL("https://github.com/xaanit/RandomUtilities")),
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.13" % "test",
      "com.google.code.findbugs" % "jsr305" % "3.0.2"
    )
  ))

lazy val core = project in file(".") //we need a variable reference to the root project

lazy val result = Project(id = "result", base = file("result"))
  .settings(
    fork := true
  )
  .dependsOn(core)
