package com.clockservice.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.time.LocalDate
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import com.clockservice.json._
import java.time.LocalTime

class PunchingTest extends AnyFlatSpec with Matchers {

  "A PunchingEmpty" should "serialize to JSON" in {
    val p = PunchingEmpty(LocalDate.of(2021, 2, 1))
    // println(p.asJson)
    val d = parse(p.asJson.toString()).getOrElse(Json.Null)
    d.hcursor.downField("date").as[String].map(_ shouldBe "2021-02-01")
  }

  "A PunchingIn" should "serialize to JSON" in {
    val p = PunchingIn(LocalDate.of(2021, 2, 1), LocalTime.of(16, 35, 7))
    //println(p.asJson)
    val d = parse(p.asJson.toString()).getOrElse(Json.Null)
    d.hcursor.downField("date").as[String].map(_ shouldBe "2021-02-01")
    d.hcursor.downField("in").as[String].map(_ shouldBe "16:35:07")
  }

  "A PunchingOut" should "serialize to JSON" in {
    val p = PunchingOut(LocalDate.of(2021, 2, 1), LocalTime.of(16, 35, 7))
    //println(p.asJson)
    val d = parse(p.asJson.toString()).getOrElse(Json.Null)
    d.hcursor.downField("date").as[String].map(_ shouldBe "2021-02-01")
    d.hcursor.downField("out").as[String].map(_ shouldBe "16:35:07")
  }

  "A PunchingFull" should "serialize to JSON" in {
    val p = PunchingFull(
      LocalDate.of(2021, 2, 1),
      LocalTime.of(7, 35, 8),
      LocalTime.of(16, 35, 7)
    )
    //println(p.asJson)
    val d = parse(p.asJson.toString()).getOrElse(Json.Null)
    d.hcursor.downField("date").as[String].map(_ shouldBe "2021-02-01")
    d.hcursor.downField("in").as[String].map(_ shouldBe "07:35:08")
    d.hcursor.downField("out").as[String].map(_ shouldBe "16:35:07")
  }
}
