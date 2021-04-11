package dev.borowiecki.sets

import com.risksense.ipaddr.{IpAddress, IpNetwork}

object Split {

  def byLastBits(bits: Int, range: IpRange): Seq[IpRange] = {
    val newRangesSize = Math.pow(2, bits).toInt

    val firstIp = range.start
    val lastIp = range.end

    val firstSubNetElement = IpNetwork(firstIp, 32 - bits).first

    Range.Long.inclusive(firstSubNetElement, lastIp, newRangesSize).map {
      networkStart =>
        val net = IpNetwork(networkStart, 32 - bits)

        val start = Math.max(net.first, firstIp)
        val end = Math.min(net.last, lastIp)

        IpRange(IpAddress(start), IpAddress(end))

    }
  }

}
