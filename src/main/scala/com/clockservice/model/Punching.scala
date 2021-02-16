package com.clockservice.model

import java.time._

sealed trait PunchingLine

final case class PunchingEmpty(
    private val employee: String,
    private val date: LocalDate
) extends PunchingLine

final case class PunchingIn(
    private val employee: String,
    private val date: LocalDate,
    private val in: OffsetTime
) extends PunchingLine

final case class PunchingOut(
    private val employee: String,
    private val date: LocalDate,
    private val in: OffsetTime
) extends PunchingLine

/** Represent a punching line i.e
  * for a given `employee` and at a given `date`
  * an `in` value of the time it stated working
  * and an `out` value of the time it stopped.
  *
  * @param employee
  * @param in
  * @param out
  * @param date
  */
final case class PunchingFull(
    private val employee: String,
    private val date: LocalDate,
    private val in: OffsetTime,
    private val out: OffsetTime
) extends PunchingLine

object PunchingLine {

  /** Base method to build a [[Punching]]
    * - Check for null on each of the fields
    * - Uppercase the employee
    *
    * @param employee the identified of an employee
    * @param date working date
    * @param in work starting time
    * @param out work stopping time
    * @return [[Punching]]
    */
  def create(
      employee: String,
      date: LocalDate,
      in: Option[OffsetTime],
      out: Option[OffsetTime]
  ): Either[Exception, PunchingLine] = {
    def checkNull[T] = (field: T, fieldName: String) =>
      Option(field).toRight(new Exception(s"Field: $fieldName required found null"))

    for {
      e <- checkNull(employee, "[employee: String]")
      d <- checkNull(date, "[date: LocalDate]")
      i <- checkNull(in, "[in: OffsetTime]")
      o <- checkNull(out, "[out: OffsetTime]")
    } yield Punching(e.toUpperCase, d, i, o)
  }

  def apply(
      employee: String,
      date: LocalDate,
      in: OffsetTime,
      out: OffsetTime
  ): PunchingLine = create(employee, date, in, out).fold(throw _, identity)
}
