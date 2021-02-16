package com.clockservice.services

import com.clockservice.model.request._
import com.clockservice.model.MonthlyReport

import cats.effect.IO

trait ClockService {
  def putLine(param: PunchingParam): IO[Unit]
  def getReport(param: ReportParam): IO[MonthlyReport]
}
