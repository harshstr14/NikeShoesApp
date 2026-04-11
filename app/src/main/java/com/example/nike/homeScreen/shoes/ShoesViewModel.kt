package com.example.nike.homeScreen.shoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nike.homeScreen.Shoe
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ShoesViewModel : ViewModel() {
    private val repository = ShoesRepository()

    private val _shoes = MutableLiveData<List<Shoe>>()
    val shoes: LiveData<List<Shoe>> = _shoes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private var allShoes: List<Shoe> = emptyList()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        fetchShoes(category)
    }

    init {
        observeSearch()
        fetchShoes("All")
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400) // 🔥 400ms delay
                .distinctUntilChanged()
                .collectLatest { query ->
                    applyFilters(query)
                }
        }
    }

    private fun fetchShoes(category: String) {
        _loading.value = true

        repository.getShoesByCategory(
            category,
            onSuccess = {
                allShoes = it
                applyFilters(_searchQuery.value)
                _loading.value = false
            },
            onFailure = {
                _loading.value = false
            }
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun applyFilters(query: String) {
        val filtered = allShoes.filter { shoe ->
            shoe.name.contains(query, ignoreCase = true)
        }

        _shoes.postValue(filtered)
    }
}