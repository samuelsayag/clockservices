package com.clockservice.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.time.LocalDate
import com.clockservice.json._
import io.circe._
import io.circe.syntax._
import io.circe.parser._

class ReportTest extends AnyFlatSpec with Matchers {

  "A MonthlyReport" should "serialize to JSON" in {
    val p = MonthlyReport(
      "John Doe",
      2021,
      2,
      List(PunchingEmpty(LocalDate.of(2021, 2, 1)))
    )
    //println(p.asJson)
    val d = parse(p.asJson.toString()).getOrElse(Json.Null)
    d.hcursor.downField("employee").as[String].map(_ shouldBe "John Doe")
    d.hcursor.downField("year").as[Int].map(_ shouldBe 2021)
    d.hcursor.downN(0).downField("date").as[String].map(_ shouldBe "2021-02-01")
    d.hcursor.downField("workingHours").as[Long].map(_ shouldBe 0L)
  }
}
