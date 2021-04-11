package dev.borowiecki

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.{ExclusiveBuilder, IpRange, NeighboursCombiner, Split}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.SortedSet

class SplitSpec extends AnyWordSpec with Matchers {

  "Split.byLastBits" should {

    "range don't start at the beginning and also don't end at the end but range can by divided by 2^bits" in {
      val range = IpRange(IpAddress("192.2.84.42"), IpAddress("192.2.86.42"))

      val res = Split.byLastBits(8, range)

      res should be(
        Vector(
          IpRange(IpAddress("192.2.84.42"), IpAddress("192.2.84.255")),
          IpRange(IpAddress("192.2.85.0"), IpAddress("192.2.85.255")),
          IpRange(IpAddress("192.2.86.0"), IpAddress("192.2.86.42"))
        )
      )
    }

    "works for bigger range A" in {
      val range = IpRange(IpAddress("197.203.0.0"), IpAddress("197.203.4.255"))

      val res = Split.byLastBits(8, range)

      println(res)
      res should not be empty

      NeighboursCombiner.fold(SortedSet.empty[IpRange] ++ res) should be(List(range))
    }

    "works for bigger range B" in {
      val range = IpRange(IpAddress("197.203.0.0"), IpAddress("197.206.9.255"))

      val res = Split.byLastBits(8, range)

      println(res)
      res should not be empty

      NeighboursCombiner.fold(SortedSet.empty[IpRange] ++ res) should be(List(range))
    }

    "return identity when input has one address" in {

      val range = IpRange(IpAddress("198.0.0.10"), IpAddress("198.0.0.10"))

      val res = Split.byLastBits(4, range)

      res should be(Seq(range))
    }

    "return all address grouped in pairs" in {

      val range = IpRange(IpAddress("198.0.0.10"), IpAddress("198.0.0.13"))

      val res = Split.byLastBits(1, range)

      res should be(
        Vector(
          IpRange(IpAddress("198.0.0.10"), IpAddress("198.0.0.11")),
          IpRange(IpAddress("198.0.0.12"), IpAddress("198.0.0.13"))
        )
      )

    }

    "return all address grouped in pairs (except one) even for odd number of addresses range " in {

      val range = IpRange(IpAddress("198.0.0.10"), IpAddress("198.0.0.14"))

      val res = Split.byLastBits(1, range)

      res should be(
        Vector(
          IpRange(IpAddress("198.0.0.10"), IpAddress("198.0.0.11")),
          IpRange(IpAddress("198.0.0.12"), IpAddress("198.0.0.13")),
          IpRange(IpAddress("198.0.0.14"), IpAddress("198.0.0.14"))
        )
      )

    }

    "return all address grouped in 4s when use 2 bits and first address starts from 0" in {

      val range = IpRange(IpAddress("198.0.0.0"), IpAddress("198.0.0.7"))

      val res = Split.byLastBits(2, range)

      res should be(
        Vector(
          IpRange(IpAddress("198.0.0.0"), IpAddress("198.0.0.3")),
          IpRange(IpAddress("198.0.0.4"), IpAddress("198.0.0.7")),
        )
      )

    }

    "return all address grouped up to 4 elements when use 2 bits but first address starts do not start at 0" in {

      val range = IpRange(IpAddress("198.0.0.3"), IpAddress("198.0.0.12"))

      val res = Split.byLastBits(2, range)

      res should be(
        Vector(
          IpRange(IpAddress("198.0.0.3"), IpAddress("198.0.0.3")),
          IpRange(IpAddress("198.0.0.4"), IpAddress("198.0.0.7")),
          IpRange(IpAddress("198.0.0.8"), IpAddress("198.0.0.11")),
          IpRange(IpAddress("198.0.0.12"), IpAddress("198.0.0.12")),
        )
      )

    }

  }

}
