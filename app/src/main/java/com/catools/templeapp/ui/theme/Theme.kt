package com.catools.templeapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TempleGold = Color(0xFFB87900)
val TempleDeepGold = Color(0xFF7A4A00)
val TempleCream = Color(0xFFFFFBF5)
val TempleSoftGold = Color(0xFFFFEBC8)
val TempleBrown = Color(0xFF5B3900)
val TempleMuted = Color(0xFF756C61)

private val TempleLight = lightColorScheme(
    primary = TempleGold,
    onPrimary = Color.White,
    primaryContainer = TempleSoftGold,
    onPrimaryContainer = TempleBrown,
    secondary = Color(0xFFD69A1C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF0D2),
    onSecondaryContainer = Color(0xFF4B3000),
    background = TempleCream,
    onBackground = Color(0xFF201B15),
    surface = Color.White,
    onSurface = Color(0xFF201B15),
    surfaceVariant = Color(0xFFF7EFE3),
    onSurfaceVariant = TempleMuted,
    outline = Color(0xFFD8C7AC)
)

@Composable
fun TempleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TempleLight,
        content = content
    )
}
