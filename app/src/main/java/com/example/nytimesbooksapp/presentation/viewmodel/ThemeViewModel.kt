package com.example.nytimesbooksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nytimesbooksapp.data.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    val isDark = prefs.isDarkMode.stateIn( // converting the Flow into a StateFlow , making usable in compose
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            prefs.setTheme(isDark)
        }
    }
}
