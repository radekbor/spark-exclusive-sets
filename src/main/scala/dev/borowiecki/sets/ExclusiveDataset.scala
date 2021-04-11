package dev.borowiecki.sets

import org.apache.spark.sql.{Dataset, SparkSession}

object ExclusiveDataset {

  def build(spark: SparkSession,
            ipRanges: Dataset[IpRange],
            bits: Int): Dataset[IpRange] = {
    import spark.implicits._

    ipRanges
      .flatMap(ipRangeEncoded => {
        Split
          .byLastBits(bits, ipRangeEncoded)
      })
      .groupByKey({
        case IpRange(s, _) =>
          KeyGenerator.usingIpMask(bits)(s)
      })
      .mapGroups((_, group) => {

        group.toList match {
          case Nil           => Nil
          case single :: Nil => single :: Nil
          case list          => ExclusiveBuilder.exclusive(bits, list)
        }

      })
      // pair repartition and mapPartitions could be done in multiple times
      // so we can reduce number of partitions gradually that could eliminate potential memory problems and be faster
      .repartition(1)
      .mapPartitions(PartitionsCombiner.combine)

  }
}
