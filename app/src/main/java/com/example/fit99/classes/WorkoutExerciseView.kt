package com.example.fit99.classes

data class WorkoutExerciseView(
    var exerciseName: String = "",
    var imageUrl : String = "",
    var mode: String = "",
    val steps: ArrayList<String> = ArrayList<String>(),
    var reps: Int = 0,
    var duration:Double = 0.00,
    var sets: Int = 0,
    var interval: Int = 0
)
