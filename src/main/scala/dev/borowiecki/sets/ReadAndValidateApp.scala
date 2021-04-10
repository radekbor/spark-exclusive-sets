package dev.borowiecki.sets

import org.apache.spark.sql.SparkSession

import scala.io.Source
import scala.util.Try

/**
  * This executable class will read and print into console mutually exclusive ip ranges.
  * It requires at least one argument that is file path,
  * If second argument provided, it will be treated as validation data
  * */
object ReadAndValidateApp {

  private val defaultNumOfBits = 4

  def main(args: Array[String]): Unit = {
    println(s"args:")
    args.foreach(println)

    val parsedArgs = args
      .sliding(2, 2)
      .toList
      .collect {

        case Array("--bits", bits) =>
          "bits" -> bits
        case Array("--txt-source", sourceTxt: String) =>
          "sourceTxt" -> sourceTxt
        case Array("--txt-expected", expectedTxt: String) =>
          "expectedTxt" -> expectedTxt
        case Array("--dir-target", targetDir: String) =>
          "targetDir" -> targetDir
      }
      .toMap

    val bits = parsedArgs
      .get("bits")
      .flatMap(raw => Try(raw.toInt).toOption)
      .getOrElse(defaultNumOfBits)

    println(s"num of bits $bits")

    parsedArgs.get("sourceTxt") match {
      case None =>
      case Some(pathToFile) =>
        println(s"App will process: $pathToFile")
        val spark = SparkSession.builder
          .appName("exclusive_ip_ranges")
          .getOrCreate()

        val calculateDs = ExclusiveDataset
          .build(spark, pathToFile, bits)
          .cache()

        val collected = calculateDs
          .collect()
          .toList


        println("collected")
        collected.foreach(println(_))

        parsedArgs.get("targetDir") match {
          case Some(targetDir) =>
            calculateDs.write
              .format("csv")
              .save(targetDir)
          case None => Unit
        }

        parsedArgs.get("expectedTxt") match {
          case Some(pathToExpected) =>
            // TODO improve closing
            val expected = Source
              .fromFile(pathToExpected)
              .getLines
              .flatMap(RangeParser.parse)
              .toList

            println("expected:")
            expected.foreach(println(_))

            assert(collected == expected)
            println("assertion passed")

          case None => Unit
        }

        spark.stop()

    }

  }

}
