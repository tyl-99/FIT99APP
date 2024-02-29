package com.example.fit99.classes

import com.google.firebase.Timestamp


data class Chat(
    val sender: String = "",
    val message: String = "",
    val receiver: String = "",
    val date : Timestamp = Timestamp.now(),
    val type : String = ""
)
