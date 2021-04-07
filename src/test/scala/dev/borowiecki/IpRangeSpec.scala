package dev.borowiecki

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.IpRange
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class IpRangeSpec extends AnyWordSpec with Matchers {

  "IpRange combine" should {
    "return only one object when combine two identical objects" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.1"))

      val res = obj.combine(obj)

      res should matchPattern {
        case (`obj`, None) =>
      }
    }

    "return only one object when combine with smaller object" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.100"))

      val res = obj.combine(other)

      res should matchPattern {
        case (`obj`, None) =>
      }
    }

    "return only one object when combine with greater object" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.100"))

      val res = other.combine(obj)

      res should matchPattern {
        case (`obj`, None) =>
      }
    }

    "return only one object when two ranges contain common part" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.100"), IpAddress("198.0.0.200"))

      val res = obj.combine(other)

      val expected = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.200"))
      res should matchPattern {
        case (`expected`, None) =>
      }
    }

    "return two one object when two ranges do not contain common part" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.200"), IpAddress("198.0.0.255"))

      val res = obj.combine(other)

      res should matchPattern {
        case (`obj`, Some(`other`)) =>
      }
    }
  }

  "IpRange reduce" should {

    "return identity when no common parts" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.200"), IpAddress("198.0.0.255"))

      val res = obj.reduce(other)

      res should be(Some(obj))
    }

    "return none when other covers obj" in {
      val obj = IpRange(IpAddress("198.0.0.2"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.129"))

      val res = obj.reduce(other)

      res should be(None)
    }

    "reduce from right side when ends with common part" in {
      val obj = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.100"), IpAddress("198.0.0.255"))

      val res = obj.reduce(other)

      res should be(
        Some(IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.99")))
      )
    }

    "reduce from left side when starts with common part" in {
      val obj = IpRange(IpAddress("198.0.0.100"), IpAddress("198.0.0.128"))
      val other = IpRange(IpAddress("198.0.0.0"), IpAddress("198.0.0.125"))

      val res = obj.reduce(other)

      res should be(
        Some(IpRange(IpAddress("198.0.0.126"), IpAddress("198.0.0.128")))
      )
    }

  }

}
