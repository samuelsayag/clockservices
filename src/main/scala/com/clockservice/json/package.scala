package com.clockservice

import java.time.LocalDate
import scala.util.Try
import io.circe.Json
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import java.time.LocalTime
import com.clockservice.model.request._
import java.time.Year
import java.time.Month
import java.time.Duration
import com.clockservice.model._

package object json {

  implicit val encodeLocalDate: Encoder[LocalDate] =
    Encoder.encodeString.contramap[LocalDate](_.toString)

  implicit val decodeLocalDate: Decoder[LocalDate] =
    Decoder.decodeString.emapTry { str =>
      Try(LocalDate.parse(str))
    }

  implicit val encodeLocalTime: Encoder[LocalTime] =
    Encoder.encodeString.contramap[LocalTime](_.toString)

  implicit val decodeLocalTime: Decoder[LocalTime] =
    Decoder.decodeString.emapTry { str =>
      Try(LocalTime.parse(str))
    }

  implicit val encodeYear: Encoder[Year] =
    Encoder.encodeInt.contramap[Year](_.getValue)

  implicit val decodeYear: Decoder[Year] = Decoder.decodeInt.emapTry { i =>
    Try(Year.of(i))
  }

  implicit val encodeMonth: Encoder[Month] =
    Encoder.encodeString.contramap[Month](_.toString)

  implicit val decodeMonth: Decoder[Month] = Decoder.decodeString.emapTry {
    str =>
      Try(Month.valueOf(str))
  }

  implicit val encodeDuration: Encoder[Duration] =
    Encoder.encodeLong.contramap[Duration](_.getSeconds)

  implicit val decodeDuration: Decoder[Duration] =
    Decoder.decodeString.emapTry { str =>
      Try(Duration.ofSeconds(java.lang.Long.parseLong(str)))
    }

  implicit val encodePe: Encoder[PunchingEmpty] = new Encoder[PunchingEmpty] {
    final def apply(a: PunchingEmpty): Json = Json.obj(
      ("date", encodeLocalDate(a.date))
    )
  }

  implicit val encodePi: Encoder[PunchingIn] = new Encoder[PunchingIn] {
    final def apply(a: PunchingIn): Json = Json.obj(
      ("date", encodeLocalDate(a.date)),
      ("in", encodeLocalTime(a.in))
    )
  }

  implicit val encodePo: Encoder[PunchingOut] = new Encoder[PunchingOut] {
    final def apply(a: PunchingOut): Json = Json.obj(
      ("date", encodeLocalDate(a.date)),
      ("out", encodeLocalTime(a.out))
    )
  }

  implicit val encodePf: Encoder[PunchingFull] = new Encoder[PunchingFull] {
    final def apply(a: PunchingFull): Json = Json.obj(
      ("date", encodeLocalDate(a.date)),
      ("in", encodeLocalTime(a.in)),
      ("out", encodeLocalTime(a.out))
    )
  }

  implicit val encodePunchingLine: Encoder[PunchingLine] = Encoder.instance {
    case pe: PunchingEmpty => encodePe(pe)
    case pi: PunchingIn    => encodePi(pi)
    case po: PunchingOut   => encodePo(po)
    case pf: PunchingFull  => encodePf(pf)
  }

  implicit val encodeMr: Encoder[MonthlyReport] = new Encoder[MonthlyReport] {
    final def apply(a: MonthlyReport): Json = Json.obj(
      ("employee", Json.fromString(a.employee)),
      ("year", encodeYear(a.year)),
      ("month", encodeMonth(a.month)),
      ("punchings", Json.fromValues(a.punchings.map(encodePunchingLine(_)))),
      ("workingHours", encodeDuration(a.workingHours))
    )
  }

  implicit val decodePunchingParam: Decoder[PunchingParam] =
    deriveDecoder[PunchingParam]

  implicit val encodePunchingParam: Encoder[PunchingParam] =
    deriveEncoder[PunchingParam]

  implicit val decodeReportParam: Decoder[ReportParam] =
    deriveDecoder[ReportParam]

  implicit val encodeReportParam: Encoder[ReportParam] =
    deriveEncoder[ReportParam]
}
