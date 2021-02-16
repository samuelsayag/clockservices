package com.clockservice.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import com.clockservice.json._
import com.clockservice.model.request.PunchingParam
import com.clockservice.utils._
import com.clockservice.model.request.ReportParam

class RequestTest extends AnyFlatSpec with Matchers {

  "Serialization of PunchingParam" should "work with employee and date" in {
    val p = PunchingParam("bob", date("2021-02-01"))
    val d = parse(p.asJson.toString).getOrElse(Json.Null).as[PunchingParam]
    d.map(v => assert(v == p)).fold(fail(_), identity)
  }

  it should "work with employee, date, in" in {
    val p = PunchingParam("bob", date("2021-02-01"), timeOp("08:30:00"))
    val d = parse(p.asJson.toString).getOrElse(Json.Null).as[PunchingParam]
    d.map(v => assert(v == p)).fold(fail(_), identity)
  }

  it should "work with employee, date, out" in {
    val p = PunchingParam("bob", date("2021-02-01"), timeOp("16:30:00"))
    val d = parse(p.asJson.toString).getOrElse(Json.Null).as[PunchingParam]
    d.map(v => assert(v == p)).fold(fail(_), identity)
  }

  it should "work with employee, date, int and out" in {

    val p = PunchingParam(
      "bob",
      date("2021-02-01"),
      timeOp("08:30:00"),
      timeOp("16:30:00")
    )
    val d = parse(p.asJson.toString).getOrElse(Json.Null).as[PunchingParam]
    d.map(v => assert(v == p)).fold(fail(_), identity)
  }

  "Serialization of ReportParam" should "work with employee and date" in {
    val p = ReportParam("bob", year(2021), month(2))
    val d = parse(p.asJson.toString).getOrElse(Json.Null).as[ReportParam]
    d.map(v => assert(v == p)).fold(fail(_), identity)
  }
}
