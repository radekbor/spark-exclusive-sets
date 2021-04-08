package dev.borowiecki.sets

import scala.annotation.tailrec

object RangeBuilder {

  @tailrec
  private def buildInternal(list: List[Int],
                            best: Option[Int] = None,
                            diff: Int = 1,
                            acc: List[(Int, Int)] = Nil): List[(Int, Int)] = {
    val args: Option[(List[Int], Option[Int], Int, List[(Int, Int)])] =
      list match {
        case Nil => None
        case f :: s :: tl if f + diff == s =>
          Some((f :: tl, Some(s), diff + 1, acc))
        case f :: tl =>
          Some((tl, None, 1, acc :+ (f, best.getOrElse(f))))
      }
    args match {
      case Some((l, b, d, a)) => buildInternal(l, b, d, a)
      case None               => acc
    }
  }

  def build(sorted: Seq[Int]): List[(Int, Int)] =
    buildInternal(sorted.toList)

}
