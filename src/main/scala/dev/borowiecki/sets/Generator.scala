package dev.borowiecki.sets

import com.risksense.ipaddr.IpAddress

import scala.util.Random

object Generator {

  private val maxSize = 256 * 256
  private val maxIp = IpAddress("255.255.255.255").numerical

  private def genRandom(random: Random = new Random()): Stream[IpRange] = {
    val start = Math.abs(random.nextLong() % maxIp - 1)
    val size = Math.abs(random.nextLong() % (maxIp - start)) % maxSize
    val end = start + size
    IpRange(start, end) #:: genRandom(random)
  }

  lazy val randos = genRandom()

}
