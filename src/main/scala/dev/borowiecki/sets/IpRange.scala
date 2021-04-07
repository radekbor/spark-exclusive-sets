package dev.borowiecki.sets

import com.risksense.ipaddr.IpAddress

case class IpRange(start: IpAddress, end: IpAddress) {

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
      if (first.end >= second.start) {
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

}
