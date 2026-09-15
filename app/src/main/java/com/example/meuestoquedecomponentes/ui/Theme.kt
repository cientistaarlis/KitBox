package com.example.meuestoquedecomponentes.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF526A3A),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD3EABB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF102004),
    secondary = androidx.compose.ui.graphics.Color(0xFF5D614F),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE1E4CD),
    surface = androidx.compose.ui.graphics.Color(0xFFFAFAF4),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE2E4D9)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFB7D39D),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF233715),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF3A5025),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD3EABB)
)

@Composable
fun MeuEstoqueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
