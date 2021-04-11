package dev.borowiecki

import dev.borowiecki.sets.{Generator, IpRange}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GeneratorSpec extends AnyWordSpec with Matchers {

  "Generator" should {

    "generate valid ip range" in {

      val res = Generator.randos.take(100).toList

      res.forall {
        case IpRange(s, e) => s < e
      } should be(true)

    }

  }

}
