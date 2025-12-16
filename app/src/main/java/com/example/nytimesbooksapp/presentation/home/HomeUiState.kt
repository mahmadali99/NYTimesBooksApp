package com.example.nytimesbooksapp.presentation.home

import com.example.nytimesbooksapp.domain.model.Book
sealed class HomeUiState{
    object Loading : HomeUiState()
    data class Success(val books: List<Book>) : HomeUiState()
    object Empty : HomeUiState()
    data class Error(val message : String): HomeUiState()
}