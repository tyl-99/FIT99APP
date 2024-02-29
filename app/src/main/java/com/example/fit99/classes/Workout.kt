package com.example.fit99.classes

data class Workout(
    var name: String = "",
    var mode: String = "",
    var email : String = "",
    var exercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
)
