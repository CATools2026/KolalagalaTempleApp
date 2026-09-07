package com.catools.templeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TempleLight = lightColorScheme(
    primary = Color(0xFF7B1F2D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DE),
    onPrimaryContainer = Color(0xFF3B0711),
    secondary = Color(0xFF9A6A13),
    secondaryContainer = Color(0xFFFFDEA3),
    background = Color(0xFFFFFBF7),
    surface = Color(0xFFFFFBF7),
    surfaceVariant = Color(0xFFF4E7E3)
)

private val TempleDark = darkColorScheme(
    primary = Color(0xFFFFB2BD),
    secondary = Color(0xFFF1BF61)
)

@Composable
fun TempleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) TempleDark else TempleLight,
        content = content
    )
}
