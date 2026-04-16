package com.example.nike.homeScreen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nike.cartScreen.CartUiState
import com.example.nike.favouriteScreen.FavouriteUiState
import com.example.nike.homeScreen.Shoe
import com.example.nike.profileScreen.UserProfile
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

    private val _cardState = MutableStateFlow<CardInfo?>(null)
    val cardState: StateFlow<CardInfo?> = _cardState

    private val _addressState = MutableStateFlow<AddressInfo?>(null)
    val addressState: StateFlow<AddressInfo?> = _addressState

    private val _favoritesState =
        MutableStateFlow<FavouriteUiState>(FavouriteUiState.Loading)
    val favoritesState: StateFlow<FavouriteUiState> = _favoritesState

    private val _cartState =
        MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartState: StateFlow<CartUiState> = _cartState

    private val _userProfileState = MutableStateFlow<UserProfile?>(null)
    val userProfileState: StateFlow<UserProfile?> = _userProfileState

    init {
        loadFavorites()
        loadCart()
        loadUserProfile()
        loadCard()
        loadAddress()
    }

    fun loadAddress() {
        viewModelScope.launch {
            repo.getAddress().collect {
                _addressState.value = it
            }
        }
    }

    fun saveAddress(
        addressLine: String,
        city: String,
        postcode: String,
        country: String
    ) {
        viewModelScope.launch {
            repo.saveAddress(addressLine, city, postcode, country).collect {
                _uiState.value = it
            }
        }
    }

    fun loadCard() {
        viewModelScope.launch {
            repo.getCard().collect {
                _cardState.value = it
            }
        }
    }

    fun saveCard(cardNumber: String, holder: String, expiry: String) {
        viewModelScope.launch {
            repo.saveCard(cardNumber, holder, expiry).collect {
                _uiState.value = it
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            repo.getUserProfile().collect {
                _userProfileState.value = it
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repo.updateUserProfile(profile).collect {
                _uiState.value = it
            }
        }
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

    fun getCartItem(shoeId: String): Flow<Shoe?> {
        return repo.getCartItemById(shoeId)
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