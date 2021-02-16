package com.clockservice.services.impl

import cats.effect.IO

import com.clockservice.services.DataSource
import com.clockservice.services.ClockService
import com.clockservice.model.{MonthlyReport, PunchingLine, Employee}
import com.clockservice.model.request._
import java.time.Year
import java.time.Month
import com.clockservice.model.PunchingFull
import com.clockservice.model.PunchingIn
import com.clockservice.model.PunchingOut
import com.clockservice.model.PunchingEmpty

class ClockServiceIO(ds: DataSource[IO]) extends ClockService {

  override def putLine(param: PunchingParam): IO[Unit] =
    for {
      pp <- validateLine(param)
      (employee, line) = pp
      _ <- ds.put(employee, line)
    } yield ()

  override def getReport(param: ReportParam): IO[MonthlyReport] =
    for {
      rp <- validateReportParam(param)
      (employee, year, month) = rp
      mr <- ds.get(employee, year, month)
    } yield mr

  /** Here can occur some validation: year, month, known employee...
    */
  def validateLine(param: PunchingParam): IO[(Employee, PunchingLine)] = IO {
    (
      Employee(param.employee),
      (param.in, param.out) match {
        case (Some(in), Some(out)) => PunchingFull(param.date, in, out)
        case (Some(in), _)         => PunchingIn(param.date, in)
        case (_, Some(out))        => PunchingOut(param.date, out)
        case _                     => PunchingEmpty(param.date)
      }
    )
  }

  def validateReportParam(param: ReportParam): IO[(Employee, Year, Month)] =
    IO((Employee(param.employee), param.year, param.month))

}
