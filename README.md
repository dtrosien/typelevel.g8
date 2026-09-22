# typelevel.g8

A [giter8](https://www.foundweekends.org/giter8/) template for a Scala 3
project built on the [Typelevel Toolkit](https://typelevel.org/toolkit/),
with [weaver](https://github.com/typelevel/weaver-test) tests and
ScalaCheck property tests.

## Usage

```
sbt new dtrosien/typelevel.g8
```

Or from a local checkout:

```
sbt new file:///path/to/typelevel.g8
```

You will be prompted for:

| Property             | Default                                   |
|----------------------|-------------------------------------------|
| `name`               | `typelevel-app`                           |
| `organization`       | `com.example`                             |
| `package`            | derived from organization and name        |
| `description`        | short project description                 |
| `scala_version`      | `3.9.0` (latest stable Scala Next)        |
| `sbt_version`        | `2.0.9`                                   |
| `toolkit_version`    | `0.2.0`                                   |
| `pureconfig_version` | `0.17.10`                                 |
| `fs2_kafka_version`  | `4.1.0`                                   |
| `weaver_version`     | `0.12.0`, the version `toolkit-test` uses |
| `github_user`        | GitHub user or org, for the POM metadata  |
| `developer_name`     | developer name for the POM                |
| `developer_email`    | developer email for the POM               |

The defaults are the latest stable releases at the time of writing; bump them
in `src/main/g8/default.properties`.

## What you get

- `build.sbt` with `toolkit`, `pureconfig-core`, `fs2-kafka`, `toolkit-test` and `weaver-scalacheck`,
  with `-Wunused:all` and deprecation warnings enabled
- `AppConfig.scala` + `application.conf`: pureconfig example with a test
- `Hello.scala`: a minimal `IOApp.Simple` printing the greeting from `AppConfig`
- `HelloSuite.scala`: a weaver `SimpleIOSuite`
- `PropertySuite.scala`: ScalaCheck property tests via weaver `Checkers`
- `.scalafmt.conf` plus the sbt-scalafmt plugin (`sbt scalafmtAll`, `sbt scalafmtCheckAll`)
- sbt-scoverage (`sbt "coverage; testFull; coverageReport"`) and sbt-ci-release
  (`.github/workflows/release.yml`, versions from git tags)
- `.mcp.json`: Metals MCP server (`metals-mcp` over stdio) for Claude Code
- `.claude/settings.json`: permission allowlist for the build tools, MCP servers auto-approved
- `.github/workflows/ci.yml`: GitHub Actions running the format check, tests and coverage
- `.scala-steward.conf`: keeps weaver pinned to the toolkit's version
- `AGENTS.md`: project instructions for coding agents, with `CLAUDE.md`
  importing it for Claude Code
- `.claude/skills/`: skills for weaver tests, the Typelevel Toolkit and cellar
  (dependency API lookup)

## Testing the template

```
./scripts/smoke-test.sh
```

This generates a project into a temp directory with `sbt new` and runs
`scalafmtSbtCheck`, `scalafmtCheckAll`, `testFull` with coverage and `coverageReport` in it. (`sbt g8Test` does not work here: the generated project
uses sbt 2, and sbt-giter8's scripted runner only accepts projects on the
same sbt binary version as the template build.)

## Notes

`weaver_version` defaults to the weaver release that `toolkit-test` is
built against. Picking a newer weaver than the toolkit ships makes sbt's
early-semver eviction check fail the build.
