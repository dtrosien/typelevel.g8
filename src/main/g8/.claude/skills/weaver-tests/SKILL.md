---
name: weaver-tests
description: How to write and run tests in this project with weaver (SimpleIOSuite, IOSuite with shared resources, expectations) and ScalaCheck property tests via weaver-scalacheck. Use when adding, changing or debugging tests.
---

# Writing weaver tests

Tests live in `src/test/scala/...` as `object`s. The test framework is
registered in `build.sbt` as `weaver.framework.CatsEffect`.

## Basic suite

```scala
import cats.effect.*
import weaver.*

object ExampleSuite extends SimpleIOSuite:

  pureTest("no IO needed") {
    expect(1 + 1 == 2)
  }

  test("returns IO[Expectations]") {
    for
      a <- IO.pure(1)
      b <- IO.pure(2)
    yield expect(a + b == 3)
  }
```

- `pureTest` takes `Expectations`; `test` takes `IO[Expectations]`.
- `expect(cond)` is the main assertion. On failure weaver prints the source
  expression, so keep the boolean readable.
- Combine expectations with `and`: `expect(a == 1) and expect(b == 2)`.
- `expect.same(expected, actual)` gives a diff for non-trivial values.
- `expect.eql(expected, actual)` uses cats `Eq` and `Show` when available.
- `success` and `failure("msg")` are ready-made expectations.
- `ignore("reason")` inside an IO `test` body skips it (it returns `IO[Nothing]`,
  so it does not type-check in `pureTest`).

## Shared resources

Use `IOSuite` when several tests need the same resource (an HTTP client, a
temp directory, a database):

```scala
import cats.effect.*
import weaver.*

object ResourceSuite extends IOSuite:
  type Res = Ref[IO, Int]
  def sharedResource: Resource[IO, Res] = Resource.eval(Ref.of[IO, Int](0))

  test("can use the resource") { ref =>
    ref.updateAndGet(_ + 1).map(n => expect(n >= 1))
  }
```

Tests in one suite run in parallel by default. Set
`override def maxParallelism = 1` on the suite if they must be sequential.

## Property tests with ScalaCheck

Mix in `weaver.scalacheck.Checkers` and use `forall`:

```scala
import weaver.*
import weaver.scalacheck.*

object PropertySuite extends SimpleIOSuite with Checkers:

  test("reverse twice is identity") {
    forall { (xs: List[Int]) =>
      expect(xs.reverse.reverse == xs)
    }
  }
```

- `forall` accepts functions of 1 to 6 arguments with `Arbitrary` instances.
- Pass an explicit generator: `forall(Gen.choose(1, 100)) { n => ... }`.
- Tune with `override def checkConfig = CheckConfig.default.withMinimumSuccessful(200)`.

## Running

```
sbt testFull                                  # every test (sbt 2's `test` is incremental)
sbt "testOnly *HelloSuite"                    # one suite
sbt "testOnly *HelloSuite -- -o *addition*"   # only tests whose "Suite.name" matches the glob
```

`-o` (`--only`) takes a `*` glob that must match the whole `SuiteName.testName`.
There is no "failures only" output flag.
