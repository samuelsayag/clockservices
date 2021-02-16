package com.clockservice.services.impl

import java.time.{Month, Year}

import cats.effect.IO

import com.clockservice.services.DataSource
import com.clockservice.model.{Employee, MonthlyReport, PunchingLine}

class IOMemoryDataSource extends DataSource[IO] {

  private val db = MemoryDataSource()

  override def put(employee: Employee): IO[Unit] =
    IO.fromEither(db.put(employee))

  override def put(employee: Employee, line: PunchingLine): IO[Unit] =
    IO.fromEither(db.put(employee, line))

  override def get(
      employee: Employee,
      year: Year,
      month: Month
  ): IO[MonthlyReport] =
    IO.fromEither(db.get(employee, year, month))

}

object IOMemoryDataSource {
  def apply(): IOMemoryDataSource = new IOMemoryDataSource
}
