package dev.borowiecki.sets

import scala.annotation.tailrec
import scala.collection.immutable.SortedSet

object NeighboursCombiner {

  @tailrec
  private def foldNeighboursInternal(
    list: List[IpRange],
    best: Option[IpRange] = None,
    acc: List[IpRange] = Nil
  ): List[IpRange] = {
    val nextArgs: Option[(List[IpRange], Option[IpRange], List[IpRange])] =
      list match {
        case Nil => None
        case f :: s :: tl if f.end + 1 == s.start =>
          val newHead = IpRange(f.start, s.end)
          Some((newHead :: tl, Some(s), acc))
        case f :: tl =>
          Some((tl, None, acc :+ IpRange(f.start, best.getOrElse(f).end)))
      }
    nextArgs match {
      case None            => acc
      case Some((l, b, a)) => foldNeighboursInternal(l, b, a)
    }
  }

  def fold(neighbours: SortedSet[IpRange]): List[IpRange] =
    foldNeighboursInternal(neighbours.toList)

}
