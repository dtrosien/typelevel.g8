# $name$

$description$

## Overview over the project
read `README.md` if available

## Metals MCP

ALWAYS use Metals MCP tools to compile and run tests instead of relying on bash commands.
If the MCP tools are not available, report that to the user rather than silently substituting.

- `compile-file` / `compile-full` — before and after code changes
- `import-build` — ALWAYS after modifying build.sbt or other build files
- `find-dep` — dependency lookup and latest versions
- `inspect` / `glob-search` — discover symbols in the project and its libraries
- `format-file` — after code changes, to apply project style

The server is preconfigured in `.mcp.json`: Claude Code launches `metals-mcp` over stdio
for this workspace, so nothing has to be started by hand. `metals-mcp` must be on PATH
(`cs install metals-mcp`); `.claude/settings.json` approves project MCP servers automatically
(`enableAllProjectMcpServers`).

Fallback if the stdio server is unavailable: start it with a fixed port (it auto-assigns
otherwise, which breaks a saved config) and register the URL:

    metals-mcp --workspace /path/to/your/scala/project --port 8080
    claude mcp add --transport http metals http://127.0.0.1:8080/mcp

Registering a new MCP server requires restarting Claude Code before its tools appear.

## sbt

The sbt 2 runner uses the thin client by default (`--client` is a no-op): every `sbt`
call reuses a background server, which is fast but leaves that server running. To verify
that the app starts, use `sbt --server run`: it runs in the foreground and can be
interrupted with Ctrl-C.

## Formatting

Formatting is configured in `.scalafmt.conf` and applied by the sbt-scalafmt plugin.
Prefer the Metals `format-file` tool when the MCP server is connected; otherwise run
`sbt scalafmtAll` (sources) and `sbt scalafmtSbt` (build files). `sbt scalafmtCheckAll`
must pass before a change is finished.


## Cellar

Use the `cellar` skill to look up the API of a JVM dependency — it documents the commands.
Per that skill: cellar for **external** dependency APIs, Metals `inspect` for everything else.


## Stack

- Scala $scala_version$ (Scala 3 syntax: indentation-based, `given`/`using`, `enum`, `extension`)
- sbt $sbt_version$ (sbt 2)
- Typelevel Toolkit $toolkit_version$: cats, cats-effect, fs2, fs2-data-csv, http4s Ember client, circe, decline
- pureconfig $pureconfig_version$ (`pureconfig-core`; Scala 3 `derives ConfigReader`)
- Tests: weaver $weaver_version$ (`weaver-cats` via `toolkit-test`) and `weaver-scalacheck`
- sbt plugins: sbt-scalafmt, sbt-scoverage (coverage), sbt-ci-release (publishing, git-based versions)

## Layout

- `src/main/scala/$package;format="packaged"$/` – application code, entry point is `Hello` (an `IOApp.Simple` that prints the configured greeting)
- `src/test/scala/$package;format="packaged"$/` – weaver suites, one `object ... extends SimpleIOSuite` per file
- `src/main/resources/application.conf` – configuration, loaded by `AppConfig.load`
- `.claude/skills/` – detailed guides for weaver, the toolkit and cellar
- `.claude/settings.json` – allows `sbt`, `scalafmt`, `cellar`, `cs` and `metals-mcp` without prompts
- `.github/workflows/ci.yml` – CI runs the same format check, tests and coverage as below
- `.github/workflows/release.yml` – `sbt ci-release` on pushes to `main` (snapshot) and `v*` tags (release)

## Commands

```
sbt compile                              # compile main sources
sbt test                                 # incremental: failed, new or affected tests only
sbt testFull                             # run all tests
sbt "testOnly $package$.HelloSuite"      # run one suite
sbt "testOnly *HelloSuite -- -o *IO*"    # weaver: only tests whose "Suite.name" matches the glob
sbt run                                  # run the app
sbt scalafmtAll                          # format main and test sources
sbt scalafmtSbt                          # format build.sbt and project/*.sbt
sbt scalafmtCheckAll                     # fail if any Scala source is unformatted
sbt "coverage; testFull; coverageReport"  # coverage; HTML report in target/out/jvm/scala-*/$name$/scoverage-report/
sbt --server --batch testFull            # non-interactive, no background server; scripts and CI
```

Run one sbt command per invocation, or join several with semicolons inside one
quoted string: `sbt "scalafmtCheckAll; testFull"`. sbt 2 treats `sbt run test` and
even `sbt "run" "test"` as running the app with the argument `test`. After a green
run, `sbt test` reports "Passed: Total 0" because nothing changed; that is not a
failure. Use `testFull` when you need every test executed.

## Conventions

- All effects go through `cats.effect.IO`. Keep side effects out of pure code.
- Add a weaver test for every new behaviour; use `pureTest` when no IO is
  needed and `forall` from `weaver.scalacheck.Checkers` for properties.
- Never set `version`, `publishTo` or `credentials` in `build.sbt`: sbt-ci-release
  derives the version from git tags (`vX.Y.Z`) and handles publishing.
- Keep dependency versions in `build.sbt`. Do not bump `weaver-scalacheck`
  past the weaver version that `toolkit-test` depends on, or sbt's
  early-semver eviction check fails the build.
- Prefer Scala 3 syntax without braces and optional `end` markers.
- The compiler reports unused imports and parameters (`-Wunused:all`). Warnings do
  not fail the build, but leave the code you touched warning-free.
- Configuration goes in `application.conf` and is read through `AppConfig`
  (pureconfig, `derives ConfigReader`); do not read system properties directly.
- Before finishing a change, run `sbt --server --batch "scalafmtSbtCheck; scalafmtCheckAll; testFull"`
  and make sure it passes.
- NEVER use non-local returns

## Development Principles

1. Think Before Coding

Don't assume. Don't hide confusion. Surface tradeoffs.

Before implementing:

    State your assumptions explicitly. If uncertain, ask.
    If multiple interpretations exist, present them - don't pick silently.
    If a simpler approach exists, say so. Push back when warranted.
    If something is unclear, stop. Name what's confusing. Ask.

2. Simplicity First

Minimum code that solves the problem. Nothing speculative.

    No features beyond what was asked.
    No abstractions for single-use code.
    No "flexibility" or "configurability" that wasn't requested.
    No error handling for impossible scenarios.
    If you write 200 lines and it could be 50, rewrite it.
    

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

3. Surgical Changes

Touch only what you must. Clean up only your own mess.

When editing existing code:

    Don't "improve" adjacent code, comments, or formatting.
    Don't refactor things that aren't broken.
    Match existing style, even if you'd do it differently.
    If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:

    Remove imports/variables/functions that YOUR changes made unused.
    Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

4. Goal-Driven Execution

Define success criteria. Loop until verified.

Transform tasks into verifiable goals:

    "Add validation" → "Write tests for invalid inputs, then make them pass"
    "Fix the bug" → "Write a test that reproduces it, then make it pass"
    "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:

1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

5. Don't write tests that test the compiler

Examples are testing match exhaustivity, typesystem etc.

6. Always verify end-to-end output quality when fixing a bug


## Code Conventions

- Keep the style of the repo (functional, typelevel-stack, tagless final)

## Documentation

When adding or modifying APIs commands, flags, or config options, update `README.md` accordingly.