package com.example.nytimesbooksapp.presentation.home


sealed class HomeEffect {

    data class NavigateToDetails(val isbn: String) : HomeEffect()

    data class ShowMessage(val message: String) : HomeEffect()

    object RefreshCompleted : HomeEffect()
}
