package com.example.fit99.classes

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class WorkoutExercise(
    var exerciseName: String = "",
    var mode: String = "",
    var reps: Int = 0,
    var duration:Double = 0.00,
    var sets: Int = 0,
    var interval: Int = 0
) : Parcelable
