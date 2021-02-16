package com.clockservice.model

import com.clockservice._
import java.time.Year
import java.time.Month

object request {

  final case class PunchingParam(
      employee: String,
      date: DATE,
      in: Option[TIME] = None,
      out: Option[TIME] = None
  )

  final case class ReportParam(
      employee: String,
      year: Year,
      month: Month
  )

}
