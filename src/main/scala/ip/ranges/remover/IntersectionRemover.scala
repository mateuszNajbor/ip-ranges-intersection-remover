package ip.ranges.remover

import ip.ranges.remover.ApplicationProperties.{databaseHost, databasePassword, databaseUser, sendDataToElastic, useDataFromDatabase}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types.{StringType, StructType}
import play.api.libs.json.{Json, OFormat}

import java.math.BigInteger

object IntersectionRemover {

  import Env.spark.implicits._

  private val schema: StructType = new StructType()
    .add(name = "start", dataType = StringType, nullable = false)
    .add(name = "stop", dataType = StringType, nullable = false)

  private val firstDummyRow: ExtendedState = {
    val enrichedIp = EnrichedIpWithJavaBigInteger(ipAddress = new BigInteger("0"), rangeStart = false)
    val state = State(counter = 0, nextStartOpt = None, finalResult = Nil)
    ExtendedState(enrichedIp, state)
  }

  def calculate: List[(String, String)] = {
    val entryData = getData

    val newDataFrame = prepareDatasetForRemovingIntersection(entryData)

    val finalState = newDataFrame.repartition(1).reduce { (acc, extendedState) =>
      val result = removeIntersection(acc, extendedState)
      ExtendedState(extendedState.enrichedIp, result)
    }

    val finalResult = finalState.state.finalResult.flatten.map(_.convertToString())

    sendDataToElasticsearch(finalResult)

    finalResult
  }

  private def getData: Dataset[IpRangeAsString] = {
    val dataFrame = if (useDataFromDatabase) {
      getDataFromDatabase
    } else {
      getDataFromFile
    }

    dataFrame
      .as[IpRangeAsString]
      .cache
  }

  private def getDataFromFile = {
    Env.spark
      .read
      .option("sep", ",")
      .schema(schema)
      .option("header", "false")
      .csv("data/ranges.csv")
  }

  private def getDataFromDatabase = {
    Env.spark.read
      .format("jdbc")
      .option("url", s"jdbc:postgresql://$databaseHost/postgres?user=$databaseUser&password=$databasePassword")
      .option("driver", "org.postgresql.Driver")
      .option("dbtable", s"(select start, stop from public.IpRanges) t")
      .load()
  }

  private def prepareDatasetForRemovingIntersection(entryData: Dataset[IpRangeAsString]) = {
    val startDataFrame: Dataset[ExtendedState] = entryData.map(range => convertToExtendedState(range.start, rangeStart = true))
    val stopDataFrame: Dataset[ExtendedState] = entryData.map(range => convertToExtendedState(range.stop, rangeStart = false))

    val firstRowDataFrame = Env.spark.createDataFrame(List(firstDummyRow)).as[ExtendedState]
    startDataFrame.union(stopDataFrame).union(firstRowDataFrame).sort("enrichedIp.ipAddress")
  }

  private def convertToExtendedState(ip: String, rangeStart: Boolean) = {
    val enrichedIp = EnrichedIpWithJavaBigInteger(ipAddress = ImplicitConversions.ipAddressToBigInteger(ip), rangeStart = rangeStart)
    val state = State(counter = 0, nextStartOpt = None, finalResult = Nil)
    ExtendedState(enrichedIp, state)
  }

  private def removeIntersection(acc: ExtendedState, extendedState: ExtendedState) = {
    val enrichedIpRange = EnrichedIp(extendedState.enrichedIp.ipAddress, extendedState.enrichedIp.rangeStart)
    RangeCalculator.calculateNextState(enrichedIpRange, acc.state)
  }

  private def sendDataToElasticsearch(finalResult: List[(String, String)]): Unit = {
    if (sendDataToElastic) {
      val resultAsJson = convertResultToJson(finalResult)
      ElasticClient.postData("ip-ranges", resultAsJson)
      ElasticClient.client.close()
    }
  }

  private def convertResultToJson(result: List[(String, String)]): String = {
    val resultAsListOfIpRange = result.map(el => IpRangeAsString(el._1, el._2))
    val resultAsIpRanges = IpRanges(resultAsListOfIpRange)

    implicit val ipRangeFormat: OFormat[IpRangeAsString] = Json.format[IpRangeAsString]
    implicit val ipRangesFormat: OFormat[IpRanges] = Json.format[IpRanges]

    Json.toJson(resultAsIpRanges).toString()
  }

  case class IpRangeAsString(start: String, stop: String)

  case class IpRanges(ips: List[IpRangeAsString])

}