package com.clockservice.model

import com.clockservice._
import java.time._

/** The [[PunchingLine]] represent the punching information
  *  of an employee at a certain date ([[PunchingFull]])
  * The date is always present but
  * - the starting time ([[PunchingIn]]),
  * - the stopping time ([[PunchingOut]])
  * - or both may be missing ([[PunchingEmpty]]).
  */
sealed trait PunchingLine {
  def date: DATE
  def year: Year   = Year.of(date.getYear)
  def month: Month = date.getMonth
}

final case class PunchingEmpty private (date: DATE) extends PunchingLine

object PunchingEmpty {
  import PunchingLine._

  def create(check: CHECK[DATE]): DATE => Either[Exception, PunchingEmpty] =
    (d: DATE) => check(d).map(new PunchingEmpty(_))

  def apply(date: DATE): PunchingEmpty =
    create(checkDate)(date).fold(throw _, identity)
}

final case class PunchingIn private (date: DATE, in: TIME) extends PunchingLine

object PunchingIn {
  import PunchingLine._

  def create(
      checkDate: CHECK[DATE],
      checkTime: CHECK[TIME]
  ): (DATE, TIME) => Either[Exception, PunchingIn] =
    (d: DATE, t: TIME) =>
      for {
        d <- checkDate(d)
        i <- checkIn(t)
      } yield new PunchingIn(d, i)

  def apply(date: DATE, in: TIME): PunchingIn =
    create(checkDate, checkIn)(date, in).fold(throw _, identity)
}

final case class PunchingOut private (date: DATE, out: TIME)
    extends PunchingLine

object PunchingOut {
  import PunchingLine._

  def create(
      checkDate: CHECK[DATE],
      checkTime: CHECK[TIME]
  ): (DATE, TIME) => Either[Exception, PunchingOut] =
    (d: DATE, t: TIME) =>
      for {
        d <- checkDate(d)
        i <- checkOut(t)
      } yield new PunchingOut(d, i)

  def apply(date: DATE, out: TIME): PunchingOut =
    create(checkDate, checkOut)(date, out).fold(throw _, identity)
}

final case class PunchingFull private (date: LocalDate, in: TIME, out: TIME)
    extends PunchingLine

object PunchingFull {
  import PunchingLine._

  def create(
      checkDate: CHECK[DATE],
      checkTimeIn: CHECK[TIME],
      checkTimeOut: CHECK[TIME]
  ): (DATE, TIME, TIME) => Either[Exception, PunchingFull] =
    (d: DATE, tin: TIME, tout: TIME) =>
      for {
        d <- checkDate(d)
        i <- checkTimeIn(tin)
        o <- checkTimeOut(tout)
        _ <- checkInBeforeOut(i, o)
      } yield new PunchingFull(d, i, o)

  def apply(date: DATE, in: TIME, out: TIME): PunchingFull =
    create(checkDate, checkIn, checkOut)(date, in, out).fold(throw _, identity)
}

object PunchingLine {

  type CHECK[T] = T => Either[Exception, T]

  def checkNull[T](field: T, fieldName: String): Either[Exception, T] =
    Option(field).toRight(
      new Exception(s"Field: $fieldName required found null")
    )

  val checkDate = checkNull(_: DATE, "[date: LocalDate]")
  val checkIn   = checkNull(_: TIME, "[in: OffsetTime]")
  val checkOut  = checkNull(_: TIME, "[out: OffsetTime]")

  val checkInBeforeOut = (i: TIME, o: TIME) =>
    Either.cond(
      i.isBefore(o),
      (),
      new Exception("Time `in` must be inferior to time `out`")
    )

  def create(
      date: DATE,
      in: Option[TIME] = None,
      out: Option[TIME] = None
  ): Either[Exception, PunchingLine] =
    (checkDate(date), checkIn(in.orNull), checkOut(out.orNull)) match {
      case (Left(e), _, _)              => Left(e)
      case (Right(d), Left(_), Left(_)) => PunchingEmpty.create(Right(_))(d)
      case (Right(d), Right(i), Left(_)) =>
        PunchingIn.create(Right(_), Right(_))(d, i)
      case (Right(d), Left(_), Right(o)) =>
        PunchingOut.create(Right(_), Right(_))(d, o)
      case (Right(d), Right(i), Right(o)) =>
        PunchingFull.create(Right(_), Right(_), Right(_))(d, i, o)
    }
}
