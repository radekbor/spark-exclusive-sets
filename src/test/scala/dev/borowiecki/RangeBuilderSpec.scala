package dev.borowiecki

import dev.borowiecki.sets.RangeBuilder
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RangeBuilderSpec extends AnyWordSpec with Matchers {

  "RangeBuilder.build" should {

    "build range for neighbours" in {

      val res = RangeBuilder.build(Seq(1, 2, 3, 4, 5, 8, 9))

      res should be(List((1, 5), (8, 9)))
    }

  }

}
