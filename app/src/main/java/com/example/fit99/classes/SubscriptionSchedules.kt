package com.example.fit99.classes

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class SubscriptionSchedule(
    var date : String = "",
    var starttime: String = "",
    var endtime : String = "",
    var status: String = ""
)