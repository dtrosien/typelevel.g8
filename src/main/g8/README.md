# $name$

$description$

## Run

```
sbt --server run
```

## Test and format

```
sbt testFull                                  # all tests (`sbt test` is incremental in sbt 2)
sbt scalafmtAll                               # format sources
sbt "coverage; testFull; coverageReport"      # tests with a coverage report
sbt --server --batch "scalafmtSbtCheck; scalafmtCheckAll; coverage; testFull; coverageReport"   # what CI runs
```

Tests use [weaver](https://github.com/typelevel/weaver-test) with the
[Typelevel Toolkit](https://typelevel.org/toolkit/), plus ScalaCheck property
tests via `weaver-scalacheck`. Formatting is defined in `.scalafmt.conf`, and
`.github/workflows/ci.yml` runs the same checks on every push and pull request.

## Release

The version comes from git tags via sbt-ci-release: pushing `vX.Y.Z` publishes a
release, pushes to `main` publish snapshots. `.github/workflows/release.yml` needs the
`PGP_PASSPHRASE`, `PGP_SECRET`, `SONATYPE_USERNAME` and `SONATYPE_PASSWORD` secrets;
see the [sbt-ci-release](https://github.com/sbt/sbt-ci-release) README.

## Configuration

`src/main/resources/application.conf` is loaded into `AppConfig` with pureconfig.

## Working with coding agents

- `AGENTS.md` holds the project instructions; `CLAUDE.md` imports it for Claude Code.
- `.claude/skills/` documents weaver, the Typelevel Toolkit and the `cellar` CLI.
- `.mcp.json` starts a Metals MCP server (`metals-mcp`, installed with `cs install metals-mcp`)
  when Claude Code opens the project; `.claude/settings.json` allows the build tools
  without prompts.
