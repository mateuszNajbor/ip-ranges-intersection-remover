package ip.ranges.remover

import com.typesafe.config.ConfigFactory

object ApplicationProperties {

  private val config = ConfigFactory.load("application.properties")

  lazy val elasticHost: String = config.getString("elastic.host")
  lazy val elasticPort: Int = config.getInt("elastic.port")
  lazy val sendDataToElastic: Boolean = config.getBoolean("elastic.sendResultToElastic")

  lazy val useDataFromDatabase: Boolean = config.getBoolean("database.useDataFromDatabase")
  lazy val databaseHost: String = config.getString("database.host")
  lazy val databaseUser: String = config.getString("database.user")
  lazy val databasePassword: String = config.getString("database.password")

}
