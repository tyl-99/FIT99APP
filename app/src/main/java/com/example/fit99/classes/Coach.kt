package com.example.fit99.model

import com.example.fit99.classes.Qualification

data class Coach(
    val CoachID: String = "",
    val email : String = "",
    val name: String = "",
    val HireDate: String= "",
    val Specialization: String= "",
    val password: String= "",
    val Bio: String= "",
    val years: Int = 0,
    val qualification: Qualification = Qualification(),
    var Status : String = "Active"
)

