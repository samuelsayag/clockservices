package com.clockservice

import com.clockservice._
import com.clockservice.services.impl.MemoryDataSource
import com.clockservice.services.impl.IOMemoryDataSource
import cats.effect.IO
import com.clockservice.services.impl.ClockServiceIO

package object services {

  def inMemory: DataSource[Failable] = MemoryDataSource()

  def inMemoryIO: DataSource[IO] = IOMemoryDataSource()

  def inMemoryClockService: ClockService = new ClockServiceIO(inMemoryIO)
}
