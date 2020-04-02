name := "Random Utilities"
description := "A bunch of random utilities."
autoScalaLibrary := false // We don't want people using this to auto have the java SDK


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
  autoAPIMappings := true,
  organization := "it.xaan",
  crossPaths := false
)
lazy val core = Project(id = "core", base = file("core"))
  .settings(
    commonSettings,
    publishSettings,
    moduleName := "random-core"
  )
lazy val result = Project(id = "result", base = file("result"))
  .settings(
    commonSettings,
    publishSettings,
    fork := true,
    moduleName := "random-result"
  )
  .dependsOn(core)
lazy val cache = Project(id = "cache", base = file("cache"))
  .settings(
    commonSettings,
    publishSettings,
    fork := true,
    moduleName := "random-cache"
  )
  .dependsOn(core)
lazy val all = Project(id = "all", base = file("."))
  .settings(
    commonSettings,
    publishSettings,
    fork := true,
    moduleName := "random-all"
  )
  .dependsOn(core, result, cache)
  .aggregate(cache, result, core)
val devs = List(
  Developer(id = "xaanit",
    name = "Jacob Frazier",
    email = "shadowjacob1@gmail.com",
    url = new URL("https://www.xaan.it"))
)
val commonSettings = Seq(
  version := "1.0.2",
  developers := devs,
  startYear := Some(2020),
  homepage := Some(new URL("https://github.com/xaanit/RandomUtilities")),
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "junit" % "junit" % "4.13" % "test",
    "com.google.code.findbugs" % "jsr305" % "3.0.2"
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "--summary=2")
)
