package com.clockservice.model

import java.time.Duration
import java.time.Month
import scala.util.Try
import java.time.Year

/**  Report describing the activity of an employee (`in` and `out` per day)
  *   over a month of a given year.
  *
  * @param employee
  * @param year
  * @param month
  * @param punchings a list of all the day in the considered month with there in/out time if it exists.
  * @param workingHours The `workingHours` is computed from "Full" punching (with `in` AND `out` fields).
  */
final case class MonthlyReport private (
    employee: String,
    year: Year,
    month: Month,
    punchings: Iterable[PunchingLine],
    workingHours: Duration
)

object MonthlyReport {

  def create(
      employee: String,
      year: Int,
      month: Int,
      punchings: Iterable[PunchingLine]
  ): Either[Throwable, MonthlyReport] =
    (for {
      y  <- Try(Year.of(year))
      m  <- Try(Month.of(month))
      ps <- checkPunching(punchings, y, m)
    } yield new MonthlyReport(
      employee,
      y,
      m,
      ps,
      workingHoursFrom(punchings)
    )).toEither

  def apply(
      employee: String,
      year: Int,
      month: Int,
      punchings: Seq[PunchingLine]
  ): MonthlyReport =
    create(employee, year, month, punchings).fold(throw _, identity)

  def workingHoursFrom(s: Iterable[PunchingLine]): Duration =
    s.foldLeft(Duration.ofNanos(0)) { case (d, p) =>
      p match {
        case p: PunchingFull => d.plus(Duration.between(p.in, p.out))
        case _               => d
      }
    }

  def checkPunching(
      ps: Iterable[PunchingLine],
      y: Year,
      m: Month
  ): Try[Iterable[PunchingLine]] = Try {
    ps
      .foldLeft(Option.empty[Set[PunchingLine]]) { case (lp, p) =>
        (p.date.getYear == y.getValue, p.date.getMonth == m) match {
          case (true, true) => lp
          case _            => lp.map(_ + p).orElse(Some(Set(p)))
        }
      }
      .fold(ps)(sp =>
        throw new Exception(
          s"Some `puchings` have either year/month not consistent with [$y, $m], problems with [$sp]"
        )
      )
  }
}
