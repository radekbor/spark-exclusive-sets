package dev.borowiecki

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.{IpRange, RangeParser}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RangeParserSpec extends AnyWordSpec with Matchers {

  "RangeParserSpec.parse" should {

    "return Some for valid ip range" in {

      val res = RangeParser.parse("197.203.0.0, 197.206.9.255")

      res should be(
        Some(IpRange(IpAddress("197.203.0.0"), IpAddress("197.206.9.255")))
      )
    }

    "return None when only one ip" in {
      val res = RangeParser.parse("200.203.0.0")

      res should be (None)
    }

    "return None when invalid ip" in {
      val res = RangeParser.parse("299.203.0.0, 197.206.9.255")

      res should be (None)
    }

  }

}
