name := "Random Utilities"
description := "A bunch of random utilities."
autoScalaLibrary := false // We don't want people using this to auto have the java SDK


val devs = List(
  Developer(id = "xaanit",
    name = "Jacob Frazier",
    email = "shadowjacob1@gmail.com",
    url = new URL("https://www.xaan.it"))
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  licenses := Seq("GPLv3" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/xaanit/RandomUtilities"),
      "scm:git:github.com/xaanit/RandomUtilities",
      Some("scm:git:github.com/xaanit/RandomUtilities")
    )
  ),
  homepage := Some(url("https://github.com/xaanit/RandomUtilities")),
  developers := devs,
  autoAPIMappings := true
)

inThisBuild(
  Seq(
    version := "1.0.0",
    developers := devs,
    startYear := Some(2020),
    homepage := Some(new URL("https://github.com/xaanit/RandomUtilities")),
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.13" % "test",
      "com.google.code.findbugs" % "jsr305" % "3.0.2"
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  ))


lazy val core = project in file(".") //we need a variable reference to the root project

lazy val result = Project(id = "result", base = file("result"))
  .settings(
    fork := true
  )
  .dependsOn(core)

lazy val cache = Project(id = "cache", base = file("cache"))
  .settings(
    fork := true
  )
  .dependsOn(core)


