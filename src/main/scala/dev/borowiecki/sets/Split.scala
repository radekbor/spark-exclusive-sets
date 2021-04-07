package dev.borowiecki.sets

import com.risksense.ipaddr.{IpAddress, IpNetwork}

object Split {

  def byLastBits(bits: Int, range: IpRange): Seq[IpRange] = {
    val s = range.start.numerical
    val e = range.end.numerical
    val newRangesSize = Math.pow(2, bits).toInt

    val rangeSize = e - s

    val ranges = (rangeSize / newRangesSize).toInt
    val firstIp = range.start.numerical
    val lastIp = range.end.numerical

    val firstSubNetElement = IpNetwork(firstIp, 32 - bits).first

    // TODO think more about different cases
    val correct = if (firstIp == firstSubNetElement || rangeSize < newRangesSize) {
      ranges
    } else {
      ranges + 1
    }

    Range
      .inclusive(0, correct)
      .toList
      .map { i =>
        val networkStart = firstIp + (i * newRangesSize)
        val net = IpNetwork(networkStart, 32 - bits)

        val start = i match {
          case 0 => firstIp
          case _ => net.first
        }

        val end = i match {
          case `correct` => lastIp
          case _        => net.last
        }

        IpRange(IpAddress(start), IpAddress(end))
      }

  }

}
