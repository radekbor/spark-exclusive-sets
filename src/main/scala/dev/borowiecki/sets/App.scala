package dev.borowiecki.sets

import org.apache.spark.sql.SparkSession

object App {

  def main(args: Array[String]): Unit = {
    args.toList match {
      case Nil =>
        println(s"At leas one argument required")

      case pathToFile :: _ =>
        println(s"App will process: $pathToFile")
        val spark = SparkSession.builder
          .appName("myapp")
          .master("local[*]")
          .getOrCreate()

        val logData = spark.read.textFile(pathToFile).cache()
        val numAs = logData.filter(line => line.contains("a")).count()
        val numBs = logData.filter(line => line.contains("b")).count()
        println(s"Lines with a: $numAs, Lines with b: $numBs")
        spark.stop()
    }

  }

}
