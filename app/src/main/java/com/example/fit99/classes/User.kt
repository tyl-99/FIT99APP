package com.example.fit99.classes

data class User(
    var imageUrl: String? = "",
    var name: String? = "",
    var email: String? = "",
    var password: String? = "",
    var gender: String? = "",
    var height: Double? = null,
    var weight: Double? = null,
    var steps : Int? = null,
    var distance : Double? = null,
    var calories : Int? = null
)

