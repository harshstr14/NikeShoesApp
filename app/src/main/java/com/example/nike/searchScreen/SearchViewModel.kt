package com.example.nike.searchScreen

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = SearchHistoryDataStore(application)

    private val _searchHistory = mutableStateListOf<String>()
    val searchHistory: List<String> = _searchHistory

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            dataStore.historyFlow.collect { savedList ->
                _searchHistory.clear()
                _searchHistory.addAll(savedList)
            }
        }
    }

    fun addSearch(query: String) {
        if (query.isBlank()) return

        _searchHistory.remove(query)
        _searchHistory.add(0, query)

        if (_searchHistory.size > 10) {
            _searchHistory.removeAt(_searchHistory.lastIndex)
        }

        save()
    }

    fun removeSearch(query: String) {
        _searchHistory.remove(query)
        save()
    }

    fun clearHistory() {
        _searchHistory.clear()
        save()
    }

    private fun save() {
        viewModelScope.launch {
            dataStore.saveHistory(_searchHistory)
        }
    }
}