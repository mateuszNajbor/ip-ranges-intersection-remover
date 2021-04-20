package ip.ranges.remover

import java.math.BigInteger

object ImplicitConversions {

  implicit def ipAddressToBigInt(ipAddress: String) = {
    val result = ipAddress.split("\\.")
    val resultAsInt: Array[Int] = result.map((el: String) => el.toInt)
    BigInt(resultAsInt(0)) * 1000 * 1000 * 1000 +
      BigInt(resultAsInt(1)) * 1000 * 1000 +
      BigInt(resultAsInt(2)) * 1000 +
      BigInt(resultAsInt(3))
  }

  implicit def ipAddressToBigInteger(ipAddress: String) = {
    val result = ipAddress.split("\\.")
    val resultAsInt: Array[String] = result.map((el: String) => el)
    new BigInteger(resultAsInt(0)).multiply(new BigInteger("1000000000")).add(
      new BigInteger(resultAsInt(1)).multiply(new BigInteger("1000000"))).add(
      new BigInteger(resultAsInt(2)).multiply(new BigInteger("1000"))).add(
      new BigInteger(resultAsInt(3)))
  }

  implicit def bigIntToIpAddress(ipAddress: BigInt): String = {

    val segment1 = (ipAddress / 1000 / 1000 / 1000) % 1000
    val segment2 = (ipAddress / 1000 / 1000) % 1000
    val segment3 = (ipAddress / 1000) % 1000
    val segment4 = ipAddress % 1000

    List(segment1, segment2, segment3, segment4).map(el => el.min(255)).map(_.toString).mkString(".")
  }
}
