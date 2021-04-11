package dev.borowiecki.sets.readers

import com.risksense.ipaddr.IpAddress
import dev.borowiecki.sets.{IpRange, JdbcUtil}
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.util.Try

object JdbcReader {

  def read(spark: SparkSession,
           url: String,
           table: String): Option[Dataset[IpRange]] = {
    import spark.implicits._
    JdbcUtil
      .buildProperties(url)
      .map(
        properties =>
          Some(
            spark.read
              .jdbc(url, table, properties)
        )
      )
      .collect {
        case Some(frame) =>
          frame.flatMap(
            row =>
              Try {
                val start = row.getAs[String]("range_start")
                val end = row.getAs[String]("range_end")
                IpRange(IpAddress(start), IpAddress(end))
              }.toOption
          )
      }

  }

}
