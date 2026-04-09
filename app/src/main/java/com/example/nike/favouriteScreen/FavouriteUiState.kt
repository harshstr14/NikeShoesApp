package com.example.nike.favouriteScreen

import com.example.nike.homeScreen.Shoe

sealed class FavouriteUiState {
    object Loading : FavouriteUiState()
    data class Success(val data: List<Shoe>) : FavouriteUiState()
    data class Error(val message: String) : FavouriteUiState()
}