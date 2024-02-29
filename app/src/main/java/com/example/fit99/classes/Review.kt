package com.example.fit99.classes

data class Review(
    val stars: Int = 0,
    val subscriptionId: String = "",
    val planId: String = "",
    val comment: String = "",
    val imageUrl: String = "",
    val email:String=""
)

