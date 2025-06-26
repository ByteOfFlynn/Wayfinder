package com.example.wayfinder

data class Clue(
    val clueText: String,
    val hints: List<String>,
    val latitude: Double,
    val longitude: Double,
    val locationInfo: String
)
