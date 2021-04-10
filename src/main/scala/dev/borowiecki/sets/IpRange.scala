package dev.borowiecki.sets

import com.risksense.ipaddr.IpAddress

case class IpRange(start: RawIp, end: RawIp) extends Ordered[IpRange] {

  lazy val startInIpFormat: IpAddress = IpAddress(start)
  lazy val endInIpFormat: IpAddress = IpAddress(end)

  override def toString: String = s"IpRange($startInIpFormat, $endInIpFormat)"

  def size: Long = end - start + 1

  override def compare(that: IpRange): Int =
    this.start.compareTo(that.start) match {
      case 0 => this.end.compareTo(that.end)
      case x => x
    }

}

object IpRange {

  def apply(start: IpAddress, end: IpAddress): IpRange =
    new IpRange(start.numerical, end.numerical)

}
