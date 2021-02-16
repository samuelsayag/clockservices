package com

import java.time.LocalDate
import java.time.LocalTime

/** Define what is a Date and a Time at the level of the service.
  * If other date time are preferable this may be change in one point.
  */
package object clockservice {

  type Failable[T] = Either[Throwable, T]

  type DATE = LocalDate
  type TIME = LocalTime
}
