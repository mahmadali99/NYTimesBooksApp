package com.example.nytimesbooksapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nytimesbooksapp.presentation.viewmodel.ThemeViewModel

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = BlueGrey80,
    tertiary = LightBlue80
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = LightBlue40
)

@Composable
fun NYTimesBooksAppTheme(
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val isDark = themeViewModel.isDark.collectAsState().value

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = Blue80,
            secondary = BlueGrey80,
            tertiary = LightBlue80
        )
    } else {
        lightColorScheme(
            primary = Blue40,
            secondary = BlueGrey40,
            tertiary = LightBlue40
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
