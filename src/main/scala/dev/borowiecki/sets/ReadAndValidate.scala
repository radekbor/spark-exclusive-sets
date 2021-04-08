package dev.borowiecki.sets

import org.apache.spark.sql.SparkSession

import scala.io.Source

/**
  * This executable class will read and print into console mutually exclusive ip ranges.
  * It requires at least one argument that is file path,
  * If second argument provided, it will be treated as validation data
  * */
object ReadAndValidate {

  private val bits = 8

  def main(args: Array[String]): Unit = {
    args.toList match {
      case Nil =>
        println(s"At leas one argument required")

      case pathToFile :: tail =>
        println(s"App will process: $pathToFile")
        val spark = SparkSession.builder
          .appName("myapp")
          .master("local[*]")
          .getOrCreate()

        val collected =
          ExclusiveDataset
            .build(spark, pathToFile, bits)
            .collect()
            .toList

        spark.stop()

        println("collected")
        collected.foreach(println(_))
        tail match {
          case pathToExpected :: _ =>
            // TODO improve closing
            val expected = Source
              .fromFile(pathToExpected)
              .getLines
              .flatMap(RangeParser.parse)
              .toList

            assert(collected == expected)

          case Nil => Unit
        }

    }

  }

}
