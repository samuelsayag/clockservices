package com.clockservice.model


final case class MonthlyReport(
    val employee: String,
    val year: Int,
    val month: String,
    val punchings: Seq[Punching]
     
    )
