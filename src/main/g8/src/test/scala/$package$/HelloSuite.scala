package $package$

import cats.effect.*
import weaver.*

object HelloSuite extends SimpleIOSuite:

  pureTest("addition is commutative for a fixed example") {
    expect(1 + 2 == 2 + 1)
  }

  test("IO computations can be tested") {
    for
      a <- IO.pure(20)
      b <- IO.pure(22)
    yield expect(a + b == 42)
  }

  test("IO.println runs without error") {
    Hello.run.as(success)
  }
