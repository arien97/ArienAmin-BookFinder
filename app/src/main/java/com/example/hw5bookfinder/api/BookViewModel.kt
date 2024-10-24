package com.example.hw5bookfinder.api

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _bookList = MutableStateFlow<List<Book>>(emptyList())
    val bookList: StateFlow<List<Book>> = _bookList

    var isSearching = mutableStateOf(false)

    init {
        // Load suggested books on start
        viewModelScope.launch {
            try {
                _bookList.value = repository.getSuggestedBooks(apiKey = "AIzaSyBgik7HfF4gQq-WY62XRZqD1mZ7UNGqum8")
            } catch (e: Exception) {
                // Log the error
                println("Error loading suggested books: ${e.message}")
                Log.e("BookViewModel", "Error loading suggested books", e)
            }
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "Searching for books with query: $query")
                isSearching.value = true
                _bookList.value = repository.searchBooks(query, apiKey = "AIzaSyBgik7HfF4gQq-WY62XRZqD1mZ7UNGqum8")
                Log.d("BookViewModel", "Search completed: ${_bookList.value.size} books found.")
            } catch (e: Exception) {
                println("Error searching books: ${e.message}")
                Log.e("BookViewModel", "Error searching books", e)
            }
        }
    }

    fun loadSuggestedBooks() {
        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "Loading suggested books")
                isSearching.value = false
                _bookList.value = repository.getSuggestedBooks(apiKey = "AIzaSyBgik7HfF4gQq-WY62XRZqD1mZ7UNGqum8")
                Log.d("BookViewModel", "Suggested books loaded: ${_bookList.value.size} books found.")
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading suggested books", e)
            }
        }
    }
}
