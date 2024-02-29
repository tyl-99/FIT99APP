package com.example.fit99.classes

data class Exercise(
    val name: String = "",
    val category: String = "",
    val description: String ="",
    val visualURL: String = "",
    val steps: ArrayList<String> = ArrayList<String>(),
    val muscles: ArrayList<String> = ArrayList<String>(),
    val recommended_reps :String = "",
    val recommended_rest_interval : String = "",
    val recommended_sets : Int = 0,
    val recommended_weight: String = ""
)
