package ip.ranges.remover

import ip.ranges.remover.ImplicitConversions.bigIntToIpAddress

case class IpRange(start: BigInt, stop: BigInt) {
  def convertToString(): (String, String) = {
    (start, stop)
  }
}
