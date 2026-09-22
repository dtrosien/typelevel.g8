---
name: typelevel-toolkit
description: What the Typelevel Toolkit dependency provides (cats-effect, fs2, fs2-data-csv, http4s Ember client, circe, decline) and idiomatic usage in this project. Use when writing application code that does IO, streaming, HTTP, JSON, CSV or CLI parsing.
---

# Typelevel Toolkit

`"org.typelevel" %% "toolkit"` bundles these libraries; no extra
dependencies are needed for them:

| Library | Import | Use for |
|---|---|---|
| cats-core | `cats.*`, `cats.syntax.all.*` | Type classes, `traverse`, `Validated`, `NonEmptyList` |
| cats-effect | `cats.effect.*` | `IO`, `Resource`, `Ref`, `Deferred`, `IOApp` |
| fs2 | `fs2.*`, `fs2.io.file.*` | Streams, files, concurrency, `Stream[IO, A]` |
| fs2-data-csv | `fs2.data.csv.*`, `fs2.data.csv.generic.semiauto.*` | CSV decoding/encoding into case classes |
| http4s Ember client | `org.http4s.ember.client.*`, `org.http4s.*` | HTTP requests |
| http4s-circe | `org.http4s.circe.*` | JSON request/response bodies |
| circe | `io.circe.*`, `io.circe.syntax.*` | JSON encoding/decoding (use `Codec.derived`) |
| decline-effect | `com.monovore.decline.*`, `com.monovore.decline.effect.*` | Command line parsing with `CommandIOApp` |

## Entry points

```scala
import cats.effect.*

object Main extends IOApp.Simple:
  def run: IO[Unit] = IO.println("hello")
```

For exit codes and args use `IOApp` with `def run(args: List[String]): IO[ExitCode]`.
For a CLI, use `CommandIOApp` from decline-effect.

## Idioms

- Acquire anything closeable through `Resource` and `use` it; never leak
  clients or file handles.
- Prefer `IO.blocking` for blocking calls and `IO.delay`/`IO(...)` for
  side effects.
- Use `Stream` from fs2 for anything larger than memory or line oriented:
  `Files[IO].readUtf8Lines(Path("in.txt"))`.
- Build an HTTP client once at the top: `EmberClientBuilder.default[IO].build`.
- Derive JSON codecs on case classes with `derives Codec` (circe) and CSV
  row decoders with `deriveCsvRowDecoder`.
- Errors: `IO.raiseError`, `attempt`, `handleErrorWith`; model expected
  failures with `Either` or `Validated` rather than exceptions.

## Pitfalls on the pinned versions

- `import fs2.*` brings `fs2.io` into scope, so a later `import io.circe.*` fails
  ("value circe is not a member of fs2.io"). Write `import _root_.io.circe.*` or
  import only what you need from fs2.
- `deriveCsvRowDecoder` (fs2-data 1.13.0) compiles on Scala 3.9 with a deprecation
  warning about an inaccessible implicit that Scala 3.10 will stop finding. Expect
  to switch to the fs2-data release that fixes it when bumping Scala.

## Testing helpers

`toolkit-test` adds `weaver-cats`. See the `weaver-tests` skill.
