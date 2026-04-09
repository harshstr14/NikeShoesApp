package com.example.nike.homeScreen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nike.cartScreen.CartUiState
import com.example.nike.favouriteScreen.FavouriteUiState
import com.example.nike.homeScreen.Shoe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val repo = UserRepository()

    private val _uiState = MutableStateFlow<String?>(null)
    val uiState: StateFlow<String?> = _uiState

    private val _favoritesState =
        MutableStateFlow<FavouriteUiState>(FavouriteUiState.Loading)
    val favoritesState: StateFlow<FavouriteUiState> = _favoritesState

    private val _cartState =
        MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartState: StateFlow<CartUiState> = _cartState

    init {
        loadFavorites()
        loadCart()
    }

    fun addToCart(item: Shoe) {
        viewModelScope.launch {
            repo.addToCart(item).collect {
                _uiState.value = it
            }
        }
    }

    fun toggleFavorite(item: Shoe) {
        viewModelScope.launch {
            repo.toggleFavorite(item).collect {
                _uiState.value = it
            }
        }
    }

    fun observeFavorite(shoeId: String): Flow<Boolean> {
        return repo.observeFavorite(shoeId)
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repo.getFavorites()
                .onStart {
                    _favoritesState.value = FavouriteUiState.Loading
                }
                .catch { e ->
                    _favoritesState.value =
                        FavouriteUiState.Error(e.message ?: "Something went wrong")
                }
                .collect { list ->
                    _favoritesState.value = FavouriteUiState.Success(list)
                }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            repo.getCartItems()
                .onStart {
                    _cartState.value = CartUiState.Loading
                }
                .catch { e ->
                    _cartState.value =
                        CartUiState.Error(e.message ?: "Something went wrong")
                }
                .collect { list ->
                    _cartState.value = CartUiState.Success(list)
                }
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            repo.removeFromCart(itemId).collect {
                _uiState.value = it
            }
        }
    }

    fun decreaseQuantity(itemId: String) {
        viewModelScope.launch {
            repo.decreaseQuantity(itemId).collect {
                _uiState.value = it
            }
        }
    }

    fun increaseQuantity(item: Shoe) {
        viewModelScope.launch {
            repo.increaseQuantity(item).collect {
                _uiState.value = it
            }
        }
    }
}