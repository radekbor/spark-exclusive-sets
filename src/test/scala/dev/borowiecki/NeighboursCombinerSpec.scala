package dev.borowiecki

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.{IpRange, NeighboursCombiner}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.SortedSet

class NeighboursCombinerSpec extends AnyWordSpec with Matchers {

  "NeighboursCombiner combine" should {

    "return nothing for empty list" in {

      val res = NeighboursCombiner.fold(SortedSet[IpRange]())

      res should be(empty)
    }

    "return one object when combining ranges that are not neighbours" in {
      val obj1 = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.1"))
      val obj2 = IpRange(IpAddress("198.0.0.2"), IpAddress("198.0.0.2"))

      val res = NeighboursCombiner.fold(SortedSet[IpRange]() ++ Seq(obj1, obj2))

      val expected = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.2"))
      res should matchPattern {
        case `expected` :: Nil =>
      }
    }

    "return two objects when combining ranges that are not neighbours" in {
      val obj1 = IpRange(IpAddress("198.0.0.1"), IpAddress("198.0.0.1"))
      val obj2 = IpRange(IpAddress("198.0.0.3"), IpAddress("198.0.0.3"))

      val res = NeighboursCombiner.fold(SortedSet[IpRange]() ++ Seq(obj1, obj2))

      res should matchPattern {
        case `obj1` :: `obj2` :: Nil =>
      }
    }

  }

}
