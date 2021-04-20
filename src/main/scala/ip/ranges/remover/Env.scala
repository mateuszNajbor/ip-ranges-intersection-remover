package ip.ranges.remover

import org.apache.spark.sql.SparkSession

object Env {

  val spark: SparkSession = SparkSession
    .builder()
    .appName("IpRangesIntersectionRemover")
    .master("local[*]")
    .getOrCreate()
}
