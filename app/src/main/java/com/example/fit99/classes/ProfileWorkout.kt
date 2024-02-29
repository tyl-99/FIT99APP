package com.example.fit99.classes

import com.google.type.Date
import java.time.LocalDate

data class ProfileWorkout(
    val name : String ,
    val exercise : ArrayList<WorkoutExerciseView>,
    val date : LocalDate
)
