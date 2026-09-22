// sbt 2 build: bare settings are common settings and apply to every subproject,
// so no `ThisBuild /` scoping is needed.
// `version` is intentionally not set: sbt-ci-release derives it from git tags (sbt-dynver).
scalaVersion  := "$scala_version$"
organization  := "$organization$"
versionScheme := Some("early-semver")

// Publishing metadata used by sbt-ci-release.
homepage   := Some(uri("https://github.com/$github_user$/$name$"))
licenses   := List(License.Apache2)
developers := List(
  Developer(
    "$github_user$",
    "$developer_name$",
    "$developer_email$",
    uri("https://github.com/$github_user$")
  )
)

val toolkitVersion    = "$toolkit_version$"
val weaverVersion     = "$weaver_version$"
val pureconfigVersion = "$pureconfig_version$"
val fs2KafkaVersion   = "$fs2_kafka_version$"

lazy val root = rootProject
  .settings(
    name := "$name$",
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Wunused:all"
    ),
    libraryDependencies ++= Seq(
      "org.typelevel"         %% "toolkit"           % toolkitVersion,
      "com.github.pureconfig" %% "pureconfig-core"   % pureconfigVersion,
      "org.typelevel"         %% "fs2-kafka"         % fs2KafkaVersion,
      "org.typelevel"         %% "toolkit-test"      % toolkitVersion % Test,
      "org.typelevel"         %% "weaver-scalacheck" % weaverVersion  % Test
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )
