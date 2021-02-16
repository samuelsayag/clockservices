package com.clockservice.services

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.time._

import com.clockservice.services.impl.MemoryDataSource
import com.clockservice.model.Employee
import com.clockservice.model.PunchingLine
import com.clockservice.model.MonthlyReport
import com.clockservice.utils._
import PunchingLine._

class MemoryDataSourceTest extends AnyFlatSpec with Matchers {

  "A MemoryDataSource" should "not have any report doing nothing" in {
    val ds = MemoryDataSource()
    assertThrows[NoSuchElementException](
      ds.get(Employee("Bob"), Year.of(2021), Month.JANUARY)
        .fold(throw _, identity)
    )
  }

  it should "not have any report when putting just an Employee" in {
    val ds = new MemoryDataSource()
    val e  = Employee("Bob")
    ds.put(e)
    assertThrows[NoSuchElementException](
      ds.get(e, Year.of(2021), Month.JANUARY)
        .fold(throw _, identity)
    )
  }

  it should "return a report with a PunchingEmpty/In/Out" in {
    val (ds, e, y, m) =
      (new MemoryDataSource(), Employee("Bob"), Year.of(2021), Month.FEBRUARY)
    ds.put(e)

    val l = List(
      create(date(y, m, 1), time(8, 0)),
      create(date(y, m, 2), None, time(16, 30)),
      create(date(y, m, 3))
    ).collect { case Right(line) => line }.tapEach(ds.put(e, _))

    ds.get(e, Year.of(2021), Month.FEBRUARY)
      .fold(
        throw _,
        mr => {
          mr shouldBe a[MonthlyReport]
          assert(mr.year == y)
          assert(mr.month == m)
          assert(mr.workingHours == Duration.ZERO)
          assert(mr.punchings.size == 3)
        }
      )
  }

  it should "compute workingHours correctly" in {
    val (ds, e, y, m) =
      (new MemoryDataSource(), Employee("Bob"), Year.of(2021), Month.FEBRUARY)
    ds.put(e)

    val l = List(
      create(date(y, m, 1), time(8, 0), time(16, 30)),
      create(date(y, m, 2), None, time(16, 30))
    ).collect { case Right(line) => line }.tapEach(ds.put(e, _))

    ds.get(e, Year.of(2021), Month.FEBRUARY)
      .fold(
        throw _,
        mr => {
          mr shouldBe a[MonthlyReport]
          assert(mr.year == y)
          assert(mr.month == m)
          assert(mr.workingHours == Duration.ZERO.plusHours(8).plusMinutes(30))
          assert(mr.punchings.size == 2)
        }
      )
  }

  it should "modify a punch line" in {
    val (ds, e, y, m) =
      (new MemoryDataSource(), Employee("Bob"), Year.of(2021), Month.FEBRUARY)
    ds.put(e)

    val l = List(
      create(date(y, m, 1)),
      create(date(y, m, 1), time(8, 0)),
      create(date(y, m, 1), time(8, 30)),
      create(date(y, m, 1), None, time(17, 30)),
      create(date(y, m, 1), None, time(17, 0)),
      create(date(y, m, 1), time(9, 0), time(18, 30))
    ).collect { case Right(line) => line }.tapEach(ds.put(e, _))

    ds.get(e, Year.of(2021), Month.FEBRUARY)
      .fold(
        throw _,
        mr => {
          mr shouldBe a[MonthlyReport]
          assert(mr.year == y)
          assert(mr.month == m)
          assert(mr.workingHours == Duration.ZERO.plusHours(9).plusMinutes(30))
          assert(mr.punchings.size == 1)
        }
      )
  }
}
