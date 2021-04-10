package dev.borowiecki

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.KeyGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class KeyGeneratorSpec extends AnyWordSpec with Matchers {

  "KeyGenerator" should {

    "generate key of value of first ip in subnet" in {

      val key = KeyGenerator.usingIpMask(8)(IpAddress("198.0.0.128").numerical)

      val firsIp = IpAddress("198.0.0.0").numerical

      key should be(firsIp)

    }

  }

}
