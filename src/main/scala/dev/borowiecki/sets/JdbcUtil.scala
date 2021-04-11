package dev.borowiecki.sets

import java.util.Properties

object JdbcUtil {

  def buildProperties(url: String): Option[Properties] = url match {
    case x if x.startsWith("jdbc:postgresql") =>
      val driver = "org.postgresql.Driver"
      Class.forName(driver)
      val properties = new Properties()
      properties.setProperty("driver", driver)
      Some(properties)
    case _ =>
      None
  }

}
