package com.clockservice.services.impl

import com.clockservice._
import com.clockservice.model._

import scala.collection._
import java.time.Year
import java.time.Month
import com.clockservice.services.DataSource
import java.time.LocalDate
import scala.util.Try

class MemoryDataSource extends DataSource[Failable] {

  import MemoryDataSource._

  private val db: mutable.Map[Key, Value] = mutable.Map.empty[Key, Value]

  override def put(e: Employee): Failable[Unit] =
    Try(db += createEmployeeEntry(e, Year.now, LocalDate.now.getMonth)).toEither
      .map(Function.const(()))

  override def put(
      employee: Employee,
      line: PunchingLine
  ): Failable[Unit] =
    Try {
      db.getOrElseUpdate(
        employee,
        createEmployeeEntry(
          employee,
          line.year,
          line.month
        )._2
      )
      db(employee)(line.year)(line.month).update(
        line.date,
        mergeLines(
          db(employee)(line.year)(line.month)
            .getOrElse(line.date, PunchingEmpty(line.date)),
          line
        )
      )
    }.toEither

  override def get(
      employee: Employee,
      year: Year,
      month: Month
  ): Failable[MonthlyReport] =
    MonthlyReport.create(
      employee.name,
      year.getValue,
      month.getValue,
      db(employee)(year)(month).values
    )
}

object MemoryDataSource {

  type Key = Employee
  type Value =
    mutable.Map[Year, mutable.Map[Month, mutable.Map[DATE, PunchingLine]]]

  def apply(): DataSource[Failable] = new MemoryDataSource

  def createEmployeeEntry(
      e: Employee,
      y: Year,
      m: Month
  ): (Key, Value) =
    e -> mutable.Map(
      y -> mutable.Map(m -> mutable.Map.empty[DATE, PunchingLine])
    )

  def mergeLines(oldLine: PunchingLine, newLine: PunchingLine): PunchingLine = {
    if (newLine.date != oldLine.date)
      throw new Exception(
        s"Cannot merge punching line for dates [${newLine.date}, ${oldLine.date}]"
      )
    else
      (newLine, oldLine) match {
        case (PunchingIn(d, i), PunchingFull(_, _, o))  => PunchingFull(d, i, o)
        case (PunchingIn(d, i), PunchingOut(_, o))      => PunchingFull(d, i, o)
        case (PunchingOut(d, o), PunchingFull(_, i, _)) => PunchingFull(d, i, o)
        case (PunchingOut(d, o), PunchingIn(_, i))      => PunchingFull(d, i, o)
        case (line, _)                                  => line
      }
  }

  implicit val lineOrdering: Ordering[PunchingLine] =
    Ordering.fromLessThan(comp)

  def comp(p1: PunchingLine, p2: PunchingLine): Boolean =
    p1.date.compareTo(p2.date) <= 0
}
