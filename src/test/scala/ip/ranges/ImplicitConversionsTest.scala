package ip.ranges

import ip.ranges.remover.ImplicitConversions.{ipAddressToBigInt, bigIntToIpAddress}
import org.scalatest.funsuite.AnyFunSuite

class ImplicitConversionsTest extends AnyFunSuite {

  test("IpAddress to BigInt") {
    val result = ipAddressToBigInt("127.198.20.20")
    assert(result == BigInt(127198020020L))
  }

  test("BigInt to IpAddress") {
    val result = bigIntToIpAddress(BigInt(127198020020L))
    assert(result == "127.198.20.20")
  }
}
