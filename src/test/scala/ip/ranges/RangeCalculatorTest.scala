package ip.ranges

import ip.ranges.remover.ImplicitConversions.ipAddressToBigInt
import ip.ranges.remover.{IpRange, RangeCalculator}
import ip.ranges.remover.RangeCalculator.calculateRange
import org.scalatest.funsuite.AnyFunSuite

class RangeCalculatorTest extends AnyFunSuite {

  test("One range fully in other") {
    val ren1 = IpRange(1, 18)
    val ren2 = IpRange(4, 15)

    val resultRen = calculateRange(List(ren1, ren2))
    assert(resultRen == List(IpRange(1, 3), IpRange(16, 18)))
  }

  test("Partially intersecting ranges") {
    val ren1 = IpRange(1, 18)
    val ren2 = IpRange(15, 25)

    val resultRen = calculateRange(List(ren1,ren2))
    assert(resultRen == List(IpRange(1, 14), IpRange(19, 25)))
  }

  test("Not intersecting ranges") {
    val ren1 = IpRange(1, 18)
    val ren2 = IpRange(20, 25)

    val resultRen = List(ren1, ren2)
    assert(resultRen == List(IpRange(1, 18), IpRange(20, 25)))
  }

  test("Not intersecting ranges in different order") {
    val ren1 = IpRange(20, 25)
    val ren2 = IpRange(1, 18)

    val resultRen = List(ren1, ren2)
    assert(resultRen == List(IpRange(20, 25), IpRange(1, 18)))
  }

  test("Multiple ranges") {
    val list = List(
      IpRange(1, 20),
      IpRange(4, 10),
      IpRange(13, 16)
    )

    val expectedResult = List(IpRange(1, 3), IpRange(11, 12), IpRange(17, 20))

    val result = RangeCalculator.calculateRange(list)
    assert(result == expectedResult)
  }


  test("Same right end of ranges") {
    val list = List(
      IpRange("201.233.7.160", "201.233.7.168"),
      IpRange("201.233.7.164", "201.233.7.168")
    )

    val result: List[IpRange] = RangeCalculator.calculateRange(list)

    val expectedResult = List(
      IpRange("201.233.7.160", "201.233.7.163"))

    assert(result.map(_.convertToString()) == expectedResult.map(_.convertToString()))
  }

  test("Same left end of ranges") {
    val list = List(
      IpRange("201.233.7.160", "201.233.7.168"),
      IpRange("201.233.7.160", "201.233.7.164")
    )

    val result: List[IpRange] = RangeCalculator.calculateRange(list)

    val expectedResult = List(
      IpRange("201.233.7.165", "201.233.7.168"))

    assert(result.map(_.convertToString()) == expectedResult.map(_.convertToString()))
  }

  test("Full scope") {
    val list = List(
      IpRange("197.203.0.0", "197.206.9.255"),
      IpRange("197.204.0.0", "197.204.0.24"),
      IpRange("201.233.7.160", "201.233.7.168"),
      IpRange("201.233.7.164", "201.233.7.168"),
      IpRange("201.233.7.167", "201.233.7.167"),
      IpRange("203.133.0.0", "203.133.255.255")
    )

    val result: List[IpRange] = RangeCalculator.calculateRange(list)

    val expectedResult = List(
      IpRange("197.203.0.0", "197.203.255.255"),
      IpRange("197.204.0.25", "197.206.9.255"),
      IpRange("201.233.7.160", "201.233.7.163"),
      IpRange("203.133.0.0", "203.133.255.255"))

    assert(result.map(_.convertToString()) == expectedResult.map(_.convertToString()))
  }
}