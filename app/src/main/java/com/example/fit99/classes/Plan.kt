package com.example.fit99.classes

data class Plan(
    val planId: String = "",
    val coachId: String = "",
    val amount: Double = 0.0,
    val name: String = "",
    val description: String = "",
    val sessions: Int = 0,
    val hours: Int = 0,
    val status : String = "Pending"
)
