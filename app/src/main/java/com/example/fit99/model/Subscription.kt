package com.example.fit99.model

import com.example.fit99.classes.Schedule

data class Subscription(
    val userEmail: String,
    val coachId: String,
    val payment: Double,
    val planName: String,
    val schedules: ArrayList<Schedule>,
    val status: String
)

