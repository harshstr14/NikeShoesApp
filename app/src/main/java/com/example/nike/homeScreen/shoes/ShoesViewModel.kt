package com.example.nike.homeScreen.shoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nike.homeScreen.Shoe

class ShoesViewModel : ViewModel() {
    private val repository = ShoesRepository()

    private val _shoes = MutableLiveData<List<Shoe>>()
    val shoes: LiveData<List<Shoe>> = _shoes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        fetchShoes(category)
    }

    private fun fetchShoes(category: String) {
        _loading.value = true

        repository.getShoesByCategory(
            category,
            onSuccess = {
                _shoes.value = it
                _loading.value = false
            },
            onFailure = {
                _loading.value = false
            }
        )
    }
}