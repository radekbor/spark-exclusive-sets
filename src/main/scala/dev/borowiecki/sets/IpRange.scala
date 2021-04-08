package dev.borowiecki.sets

import com.risksense.ipaddr.IpAddress

case class IpRange(start: RawIp, end: RawIp) extends Ordered[IpRange] {

  lazy val startInIpFormat: IpAddress = IpAddress(start)
  lazy val endInIpFormat: IpAddress = IpAddress(end)

  override def toString: String = s"IpRange($startInIpFormat, $endInIpFormat)"

  def size: Long = end - start + 1

  def reduce(other: IpRange): Option[IpRange] = {
    val left = if (this.start < other.start) {
      this.start
    } else {
      // cut everything from other.start to other.end
      other.end + 1
    }

    val right = if (this.end > other.end) {
      this.end
    } else {
      // cut everything from other.start to other.end
      other.start - 1
    }

    if (left <= right) {
      val leftReducedToOriginalArea = if (left < this.start) {
        this.start
      } else {
        left
      }

      val rightReducedToOriginalArea = if (right > this.end) {
        this.end
      } else {
        right
      }
      Some(IpRange(leftReducedToOriginalArea, rightReducedToOriginalArea))
    } else {
      None
    }

  }

  def combine(other: IpRange): (IpRange, Option[IpRange]) = {
    if (this == other) {
      (this, None)
    } else if (this.contains(other)) {
      (this, None)
    } else if (other.contains(this)) {
      (other, None)
    } else {
      val (first, second) = if (this.start < other.start) {
        (this, other)
      } else {
        (other, this)
      }
      if (first.end >= second.start - 1) {
        // overlapping
        (IpRange(first.start, second.end), None)
      } else {
        (this, Some(other))
      }
    }
  }

  private def contains(other: IpRange): Boolean = {
    this.start <= other.start && other.end <= this.end
  }

  override def compare(that: IpRange): Int =
    this.start.compareTo(that.start) match {
      case 0 => this.end.compareTo(that.end)
      case x => x
    }

}

object IpRange {

  def apply(start: IpAddress, end: IpAddress): IpRange =
    new IpRange(start.numerical, end.numerical)

//  @tailrec
//  private def combineRec(acc: List[IpRange], arg: List[IpRange]): List[IpRange] = {
//    arg match {
//      case Nil         => acc
//      case head :: Nil => head :: acc
//      case first :: second :: tail =>
//        val (newAcc, arg) = first.combine(second) match {
//          case (res, None)       => (acc, res :: tail)
//          case (res, Some(next)) => (res :: acc, next :: tail)
//        }
//        combineRec(newAcc, arg)
//    }
//  }
//
//  def combineMany(seq: Seq[IpRange]): List[IpRange] = {
//
//    combineRec(List.empty, seq.toList).reverse
//
//  }

}
