package com.example.nike.homeScreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shoe(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val imageURL: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val productDetails: List<String> = emptyList(),
    val shoeImages: List<String> = emptyList()
) : Parcelable
