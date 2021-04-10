package dev.borowiecki.sets

import com.risksense.ipaddr.{IpAddress, IpNetwork}

object ExclusiveBuilder {

  def exclusive(bits: Int, seq: Seq[IpRange]): Seq[IpRange] = {
    seq.headOption match {

      case None => Seq.empty
      case Some(head) =>
        val mask = IpNetwork(head.start, 32 - bits)

        val unique = lastBitsHistogram(bits, seq)
          .filter(_.count == 1)
          .map(_.bits)

        RangeBuilder
          .build(unique)
          .map {
            case (start, end) =>
              val startIp = IpAddress(mask.first ^ start)
              val endIp = IpAddress(mask.first ^ end)
              IpRange(startIp, endIp)
          }

    }

  }

  case class OccurrencesAndBits(count: Byte, bits: Int)

  /**
    * Due to optimization it make no sense to count accurate result, it is better to do mark 0, 1 or more occurrences
    * */
  private def lastBitsHistogram(bits: Int,
                                seq: Seq[IpRange]): Seq[OccurrencesAndBits] = {

    val occurrences = Array.tabulate(Math.pow(2, bits).toInt) { _ =>
      0.byteValue()
    }

    seq.foreach {
      case IpRange(s, e) =>
        val net = IpNetwork(s, 32 - bits)
        val first = (s ^ net.first).toInt
        val last = (e ^ net.first).toInt
        Range.inclusive(first, last).foreach { x =>
          occurrences(x) = occurrences(x) match {
            case 0 => 1
            case 1 => 2
            case y => y
          }
        }
    }
    occurrences.toList.zipWithIndex
      .map {
        case (num, lastBits) =>
          OccurrencesAndBits(num, lastBits)
      }
  }

}
