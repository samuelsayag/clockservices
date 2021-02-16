package com.clockservice

import java.time.LocalTime
import java.time.LocalDate
import java.time.Year
import java.time.Month

package object utils {

  def time(t: String): LocalTime = LocalTime.parse(t)

  def timeOp(t: String): Option[LocalTime] = Some(LocalTime.parse(t))

  def date(d: String): LocalDate = LocalDate.parse(d)

  def dateOp(d: String): Option[LocalDate] = Some(LocalDate.parse(d))

  def year(i: Int): Year = Year.of(i)

  def month(i: Int): Month = Month.of(i)

  val date = (y: Year, m: Month, d: Int) =>
    LocalDate.of(y.getValue, m.getValue, d)

  val time = (h: Int, m: Int) => Some(LocalTime.of(h, m))
}
