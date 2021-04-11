package dev.borowiecki.sets.writers

import dev.borowiecki.sets.{IpRange, JdbcUtil}
import org.apache.spark.sql.{Dataset, SparkSession}

object JdbcWriter {

  def write(dataset: Dataset[IpRange], url: String, table: String)(
    implicit sparkSession: SparkSession
  ): Unit = {
    import sparkSession.implicits._
    JdbcUtil.buildProperties(url) match {
      case Some(properties) =>
        dataset
          .map(_.pretty)
          .write
          .jdbc(url, table, properties)
      case None =>
        Unit
    }

  }
}
