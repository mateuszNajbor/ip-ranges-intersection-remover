package ip.ranges

import ip.ranges.remover.{Env, IntersectionRemover}
import org.apache.log4j.{Level, Logger}

object IpRangesIntersectionRemoverBoot extends App {

  Logger.getLogger("org").setLevel(Level.ERROR)

  val result = IntersectionRemover.calculate
  println(s"Result ip ranges: $result")

  Env.spark.stop()
}


