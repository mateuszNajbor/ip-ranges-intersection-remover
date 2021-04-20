package ip.ranges.remover

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types.{StringType, StructType}

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

  def count: List[(String, String)] = {
    val entryData = getData

    val newDataFrame = prepareDatasetForRemovingIntersection(entryData)

    val finalState = newDataFrame.repartition(1).reduce { (acc, extendedState) =>
      val result = removeIntersection(acc, extendedState)
      ExtendedState(extendedState.enrichedIp, result)
    }

    finalState.state.finalResult.flatten.map(_.convertToString())
  }

  private def getData: Dataset[IpRangeAsString] = {
    Env.spark
      .read
      .option("sep", ",")
      .schema(schema)
      .option("header", "false")
      .csv("data/ranges.csv")
      .as[IpRangeAsString]
      .cache
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

  case class IpRangeAsString(start: String, stop: String)

}