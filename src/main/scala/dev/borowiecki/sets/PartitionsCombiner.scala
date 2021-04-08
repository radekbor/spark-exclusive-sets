package dev.borowiecki.sets

import scala.collection.immutable.SortedSet

object PartitionsCombiner {

  def combine(partitions: Iterator[Seq[IpRange]]): Iterator[IpRange] = {
    val nonEmptyPartitions = partitions.filter(_.nonEmpty)
    if (nonEmptyPartitions.isEmpty) {
      Iterator.empty
    } else {
      val x = nonEmptyPartitions
        .map(SortedSet[IpRange]() ++ _)
        .toList

      val neighbours = x
        .reduce((a, b) => a ++ b)
      NeighboursCombiner.fold(neighbours).toIterator
    }
  }

}
