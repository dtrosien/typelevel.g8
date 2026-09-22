// Build for the template itself (not for generated projects).
// Provides `sbt g8` to preview the applied template under target/g8.
// Note: `g8Test` cannot run here because the generated project uses sbt 2
// while sbt-giter8's scripted runner only accepts projects on the same sbt
// binary version. Use scripts/smoke-test.sh instead.
lazy val root = (project in file("."))
  .enablePlugins(Giter8Plugin)
  .settings(
    name         := "typelevel.g8",
    scalaVersion := "2.12.21"
  )
