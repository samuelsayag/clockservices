package com.clockservice

import io.finch._
import org.scalatest.funsuite.AnyFunSuite
import com.clockservice.model._
import com.clockservice.model.request._
import io.circe.syntax._
import com.clockservice.json._
import com.clockservice.utils._
import com.twitter.io.Buf
import com.clockservice.services._
import java.time.Duration

class MainTest extends AnyFunSuite {

  val reportParam: String =
    ReportParam("bob", year(2021), month(2)).asJson.toString()
  val punchingEmpty: PunchingParam = PunchingParam("bob", date("2021-02-01"))
  val punchingIn: PunchingParam =
    PunchingParam("bob", date("2021-02-01"), Some(time("08:00")))
  val punchingOut: PunchingParam =
    PunchingParam("bob", date("2021-02-01"), None, Some(time("08:00")))
  val punchingFullD1_1: PunchingParam = PunchingParam(
    "bob",
    date("2021-02-01"),
    Some(time("08:00")),
    Some(time("16:30"))
  )
  val punchingFullD1_2: PunchingParam = PunchingParam(
    "bob",
    date("2021-02-01"),
    Some(time("08:00")),
    Some(time("17:30"))
  )
  val punchingFullD2: PunchingParam = PunchingParam(
    "bob",
    date("2021-02-02"),
    Some(time("10:00")),
    Some(time("21:00"))
  )

  test(
    """No data and request report. Yield no content (= Exception in the IO execution)"""
  ) {
    val cs = inMemoryClockService
    val req = Main.report(cs)(
      Input
        .post(s"/${RoutePath.report}/${RoutePath.month}")
        .withBody[Application.Json](Buf.Utf8(reportParam))
    )
    assertThrows[Exception](req.awaitValueUnsafe())
  }

  test("""Line insertion. Yield an empty json""") {
    val cs = inMemoryClockService
    val p  = punchingEmpty.asJson.toString
    val req =
      Main.line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p)))
    val res = req.awaitValueUnsafe()
    assert(res == Some(()))
  }

  test(
    """Empty line + request report. Yield report empty (0h working hours)"""
  ) {
    val cs = inMemoryClockService
    val p  = punchingEmpty.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p)))
      .awaitValueUnsafe()
    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(report.workingHours == Duration.ZERO)
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }

  test(
    """Punching in + request report. Yield report empty (0h working hours)"""
  ) {
    val cs = inMemoryClockService
    val p  = punchingIn.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p)))
      .awaitValueUnsafe()
    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(report.workingHours == Duration.ZERO)
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }

  test(
    """Punching out + request report. Yield report empty (0h working hours)"""
  ) {
    val cs = inMemoryClockService
    val p  = punchingOut.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p)))
      .awaitValueUnsafe()
    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(report.workingHours == Duration.ZERO)
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }

  test(
    """Punching full + request report. Yield report empty (8h30m working hours)"""
  ) {
    val cs = inMemoryClockService
    val p  = punchingFullD1_1.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p)))
      .awaitValueUnsafe()
    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(
          report.workingHours == Duration.ZERO.plusHours(8).plusMinutes(30)
        )
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }

  test(
    """Punching full x 2 (same day) + request report. Yield report empty (9h30m working hours)"""
  ) {
    val cs = inMemoryClockService
    val p1 = punchingFullD1_1.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p1)))
      .awaitValueUnsafe()

    val p2 = punchingFullD1_2.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p2)))
      .awaitValueUnsafe()

    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(
          report.workingHours == Duration.ZERO.plusHours(9).plusMinutes(30)
        )
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }

  test(
    """Punching full x 2 (day1, day2) + request report. Yield report empty (19h30m working hours)"""
  ) {
    val cs = inMemoryClockService
    val p1 = punchingFullD1_1.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p1)))
      .awaitValueUnsafe()

    val p2 = punchingFullD2.asJson.toString
    Main
      .line(cs)(Input.post("/line").withBody[Application.Json](Buf.Utf8(p2)))
      .awaitValueUnsafe()

    val res = Main
      .report(cs)(
        Input
          .post(s"/${RoutePath.report}/${RoutePath.month}")
          .withBody[Application.Json](Buf.Utf8(reportParam))
      )
      .awaitValueUnsafe()
    res match {
      case None => fail("should have a report")
      case Some(report) => {
        assert(
          report.workingHours == Duration.ZERO.plusHours(19).plusMinutes(30)
        )
        assert(report.employee == "bob")
        assert(report.year == year(2021))
        assert(report.month == month(2))
      }
    }
  }
}
