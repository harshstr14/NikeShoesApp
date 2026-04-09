package com.example.nike.cartScreen

import com.example.nike.homeScreen.Shoe

sealed class CartUiState {
    object Loading : CartUiState()
    data class Success(val data: List<Shoe>) : CartUiState()
    data class Error(val message: String) : CartUiState()
}