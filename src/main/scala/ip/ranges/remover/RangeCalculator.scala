package ip.ranges.remover

object RangeCalculator {

  def calculateRange(ranges: List[IpRange]): List[IpRange] = {
    val enrichedIps = enrichWithRangeStartBoolean(ranges).sortBy(_.ipAddress)

    val ipRanges = enrichedIps.foldLeft(State(0, None, Nil)) { (state, el) =>
      calculateNextState(el, state)
    }
    ipRanges.finalResult.flatten
  }

  def calculateNextState(enrichedIp: EnrichedIp, state: State): State = {
    if (enrichedIp.isStartRange) {
      val (newIpRange, nextStart) = calculateIpRangeForCounterUp(enrichedIp, state.counter, state.nextStartOpt)
      State(state.counter + 1, nextStart, state.finalResult :+ newIpRange)
    } else {
      val (newIpRange, nextStart) = calculateIpRangeForCounterDown(enrichedIp, state.counter, state.nextStartOpt)
      State(state.counter - 1, nextStart, state.finalResult :+ newIpRange)
    }
  }

  private def calculateIpRangeForCounterUp(enrichedIp: EnrichedIp, counter: Int, nextStartOpt: Option[BigInt]) = {
    if (counter == 1) {
      val nextStart = nextStartOpt.getOrElse(throw new RuntimeException("Cannot find nextStart"))
      if (enrichedIp.ipAddress - 1 >= nextStart) {
        (Some(IpRange(nextStart, enrichedIp.ipAddress - 1)), None)
      } else {
        (None, nextStartOpt)
      }
    } else {
      if (counter == 0) {
        (None, Some(enrichedIp.ipAddress))
      } else {
        (None, nextStartOpt)
      }
    }
  }

  private def calculateIpRangeForCounterDown(enrichedIp: EnrichedIp, counter: Int, nextStartOpt: Option[BigInt]) = {
    if (counter == 1) {
      val nextStart = nextStartOpt.getOrElse(throw new RuntimeException("Cannot find nextStart"))
      if (enrichedIp.ipAddress >= nextStart) {
        (Some(IpRange(nextStart, enrichedIp.ipAddress)), None)
      } else {
        (None, nextStartOpt)
      }
    } else {
      if (counter == 2) {
        (None, Some(enrichedIp.ipAddress + 1))
      } else {
        (None, nextStartOpt)
      }
    }
  }

  private def enrichWithRangeStartBoolean(ranges: List[IpRange]): List[EnrichedIp] = {
    ranges.flatMap { range =>
      List(EnrichedIp(range.start, isStartRange = true), EnrichedIp(range.stop, isStartRange = false))
    }
  }
}
