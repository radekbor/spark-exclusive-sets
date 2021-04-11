package dev.borowiecki.sets

import dev.borowiecki.sets.readers.{FileReader, JdbcReader}
import dev.borowiecki.sets.writers.JdbcWriter
import org.apache.spark.sql.SparkSession

import scala.io.Source
import scala.util.Try

/**
  * This executable class will read and print into console mutually exclusive ip ranges.
  */
object ReadAndValidateApp {

  private val defaultNumOfBits = 4
  private val defaultPrinting = false
  private val defaultRandomDataSize = 0 // 0 if disabled, otherwise how many items

  def main(args: Array[String]): Unit = {
    println(s"args:")
    args.foreach(println)

    val parsedArgs = args
      .sliding(2, 2)
      .toList
      .collect {

        case Array("--bits", bits) =>
          "bits" -> bits
        case Array("--print", print) =>
          "print" -> print
        case Array("--random-input", randomInput) =>
          "randomInput" -> randomInput

        case Array("--txt-source", sourceTxt) =>
          "sourceTxt" -> sourceTxt

        case Array("--jdbc-source-url", sourceJdbcUrl) =>
          "sourceJdbcUrl" -> sourceJdbcUrl
        case Array("--jdbc-source-table", sourceJdbcTable) =>
          "sourceJdbcTable" -> sourceJdbcTable

        case Array("--jdbc-target-url", targetJdbcUrl) =>
          "targetJdbcUrl" -> targetJdbcUrl
        case Array("--jdbc-target-table", targetJdbcTable) =>
          "targetJdbcTable" -> targetJdbcTable

        case Array("--txt-expected", expectedTxt) =>
          "expectedTxt" -> expectedTxt
        case Array("--dir-target", targetDir) =>
          "targetDir" -> targetDir
      }
      .toMap

    val bits = parsedArgs
      .get("bits")
      .flatMap(raw => Try(raw.toInt).toOption)
      .getOrElse(defaultNumOfBits)

    val print = parsedArgs
      .get("print")
      .flatMap(raw => Try(raw.toBoolean).toOption)
      .getOrElse(defaultPrinting)

    val randomInputSize = parsedArgs
      .get("randomInput")
      .flatMap(raw => Try(raw.toInt).toOption)
      .getOrElse(defaultRandomDataSize)

    val validatedBits = if (bits < 1 || bits > 31) {
      println(s"incorrect num of bits $bits")
      defaultNumOfBits
    } else {
      bits
    }

    println(s"num of bits $validatedBits")
    println(s"collect and print enabled: $print")

    val spark = SparkSession.builder
      .appName("exclusive_ip_ranges")
      .getOrCreate()

    val randomInput = if (randomInputSize > 0) {
      Some {
        import spark.implicits._
        val generatedInput = Generator.randos.take(randomInputSize)
        println("randomInput")
        generatedInput.foreach(println(_))
        spark.createDataset(generatedInput)
      }
    } else {
      None
    }

    val input = randomInput
      .orElse {
        parsedArgs
          .get("sourceTxt")
          .map { pathToFile =>
            println(s"App will process: $pathToFile")
            FileReader.read(spark, pathToFile)
          }
      }
      .orElse {
        for {
          sourceJdbcUrl <- parsedArgs.get("sourceJdbcUrl")
          sourceJdbcTable <- parsedArgs.get("sourceJdbcTable")
          _ = println(s"App will process jdbc $sourceJdbcTable")
          ds <- JdbcReader.read(spark, sourceJdbcUrl, sourceJdbcTable)
        } yield ds

      }

    input match {
      case None => Unit
      case Some(inputDs) =>
        val calculateDs = ExclusiveDataset
          .build(spark, inputDs, validatedBits)
          .cache()

        parsedArgs.get("targetDir") match {
          case Some(targetDir) =>
            calculateDs.write
              .format("csv")
              .save(targetDir)
            println("save in targetDir")
          case None =>
            println("no target dir")
        }

        for {
          targetJdbcUrl <- parsedArgs.get("targetJdbcUrl")
          targetJdbcTable <- parsedArgs.get("targetJdbcTable")
        } yield
          JdbcWriter.write(calculateDs, targetJdbcUrl, targetJdbcTable)(spark)

        val expectedTxtSource = parsedArgs.get("expectedTxt")
        if (print || expectedTxtSource.isDefined) {
          val collected = calculateDs
            .collect()
            .toList
          if (print) {
            println("collected")
            collected.foreach(println(_))
          }
          expectedTxtSource match {
            case Some(pathToExpected) =>
              // TODO improve closing
              val expected = Source
                .fromFile(pathToExpected)
                .getLines
                .flatMap(RangeParser.parse(_))
                .toList

              println("expected:")
              expected.foreach(println(_))

              assert(collected == expected)
              println("assertion passed")

            case None => Unit
          }
        }

        spark.stop()

    }

  }

}
