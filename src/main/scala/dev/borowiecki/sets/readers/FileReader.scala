package dev.borowiecki.sets.readers

import dev.borowiecki.sets.{IpRange, RangeParser}
import org.apache.spark.sql.{Dataset, SparkSession}

object FileReader {

  def read(spark: SparkSession, path: String): Dataset[IpRange] = {
    val rawRanges = spark.read.textFile(path)
    import spark.implicits._

    rawRanges
      .flatMap(RangeParser.parse(_))
  }

}
