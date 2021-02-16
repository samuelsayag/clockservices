package com.clockservice

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import io.finch.catsEffect._
import com.clockservice.json._
import com.clockservice.model.request._
import com.clockservice.services._
import com.clockservice.model.MonthlyReport
import com.clockservice.model.RoutePath
import com.twitter.finagle.http.Status

object Main extends App {

  val clockservice = inMemoryClockService

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def line(clockservice: ClockService): Endpoint[IO, Unit] =
    post(RoutePath.line :: jsonBody[PunchingParam])
      .mapAsync { p =>
        clockservice.putLine(p)
      }

  def report(clockservice: ClockService): Endpoint[IO, MonthlyReport] = post(
    RoutePath.report :: RoutePath.month :: jsonBody[ReportParam]
  ).mapOutputAsync { p =>
    clockservice
      .getReport(p)
      .redeem(
        th =>
          Output.failure(
            new Exception("Error occured when requesting report", th),
            Status.NoContent
          ),
        r => Output.payload(r, Status.Ok)
      )
  }

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](line(clockservice) :+: report(clockservice))
    .toService

  Await.ready(Http.server.serve(":8081", service))
}
