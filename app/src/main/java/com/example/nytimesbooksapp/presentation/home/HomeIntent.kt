package com.example.nytimesbooksapp.presentation.home

sealed class HomeIntent {
    object Refresh : HomeIntent()
    object LoadBooks : HomeIntent()
    object ClearSearch : HomeIntent()
    data class LoadBooksByForce(val isInitial: Boolean = false): HomeIntent()
    data class Search(val query: String) : HomeIntent()
    data class LoadWithDate(val date: String) : HomeIntent()
    data class ShowDetails(val isbn: String) : HomeIntent()
}