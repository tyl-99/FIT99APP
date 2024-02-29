package com.example.fit99.classes

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Schedule(
    var date : LocalDate,
    var starttime: LocalTime,
    var endtime : LocalTime,
    var status: String
)
