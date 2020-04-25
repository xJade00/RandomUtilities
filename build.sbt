name := "Random Utilities"
description := "A bunch of random utilities."
autoScalaLibrary := false // We don't want people using this to auto have the scala SDK


lazy val primitiveSpecializations = createProject(id = "primitive-specializations", settings = Seq(moduleName := "random-primitive-specializations"))
  .dependsOn(core)
lazy val core = createProject(id = "core", settings = Seq(moduleName := "random-core"))
lazy val result = createProject(id = "result", settings = Seq(moduleName := "random-result", fork := true))
  .dependsOn(core)
lazy val cache = createProject(id = "cache", settings = Seq(moduleName := "random-cache", fork := true))
  .dependsOn(core)
lazy val all = createProject(id = "all", file = Some("."), settings = Seq(moduleName := "random-all", fork := true))
  .dependsOn(cache, result, core, primitiveSpecializations)
  .aggregate(cache, result, core, primitiveSpecializations)
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
val devs = List(
  Developer(id = "xaanit",
    name = "Jacob Frazier",
    email = "shadowjacob1@gmail.com",
    url = new URL("https://www.xaan.it"))
)
val commonSettings = Seq(
  version := "1.1.0",
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

def createProject(
                   id: String,
                   file: Option[String] = None,
                   settings: Seq[SettingsDefinition] = Seq(),
                 ): Project =
 Project(id = id, base = sbt.file(file match {
    case Some(value) => value
    case None => id
  }))
    .settings(commonSettings ++ publishSettings ++ settings: _*)

