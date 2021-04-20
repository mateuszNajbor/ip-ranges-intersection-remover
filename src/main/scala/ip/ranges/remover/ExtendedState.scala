package ip.ranges.remover

import java.math.BigInteger

case class ExtendedState(enrichedIp: EnrichedIpWithJavaBigInteger, state: State)

case class State(counter: Int, nextStartOpt: Option[BigInt], finalResult: List[Option[IpRange]])

case class EnrichedIpWithJavaBigInteger(ipAddress: BigInteger, rangeStart: Boolean)

case class EnrichedIp(ipAddress: BigInt, isStartRange: Boolean)
