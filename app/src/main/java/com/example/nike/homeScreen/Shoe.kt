package com.example.nike.homeScreen

data class Shoe(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val imageURL: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val productDetails: List<String> = emptyList()
)
