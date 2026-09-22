package $package$

import weaver.*
import weaver.scalacheck.*

object PropertySuite extends SimpleIOSuite with Checkers:

  test("integer addition is commutative") {
    forall { (a: Int, b: Int) =>
      expect(a + b == b + a)
    }
  }

  test("reversing a list twice gives the original list") {
    forall { (xs: List[Int]) =>
      expect(xs.reverse.reverse == xs)
    }
  }

  test("string concatenation preserves total length") {
    forall { (s1: String, s2: String) =>
      expect((s1 + s2).length == s1.length + s2.length)
    }
  }
