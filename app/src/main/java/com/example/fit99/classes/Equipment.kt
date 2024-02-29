package com.example.fit99.classes

data class Equipment(
    val name: String = "",
    val description: String = "",
    val imageURL: String = "",
    val exercises: ArrayList<String> = ArrayList<String>()
)
