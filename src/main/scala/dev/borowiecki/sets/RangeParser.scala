package dev.borowiecki.sets

import com.risksense.ipaddr.IpAddress

import scala.util.Try

object RangeParser {

  def parse(line: String): Option[IpRange] = {
    line.split(", ").toList match {
      case rawStart :: rawEnd :: Nil =>
        Try {
          val start = IpAddress(rawStart)
          val end = IpAddress(rawEnd)
          IpRange(start, end)
        }.toOption
      case _ =>
        None
    }
  }

}
