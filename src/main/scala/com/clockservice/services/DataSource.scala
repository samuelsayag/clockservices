package com.clockservice.services

import java.time.Month
import java.time.Year
import com.clockservice.model.MonthlyReport
import com.clockservice.model.PunchingLine
import com.clockservice.model.Employee

trait DataSource[F[_]] {
  def put(employee: Employee): F[Unit]
  def put(employee: Employee, line: PunchingLine): F[Unit]
  def get(
      employee: Employee,
      year: Year,
      month: Month
  ): F[MonthlyReport]
}
