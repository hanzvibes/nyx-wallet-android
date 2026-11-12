package com.hanzvibes.nyxwallet.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object NyxColors {
    val Background = Color(0xFF080A10)
    val Surface = Color(0xFF141823)
    val Card = Color(0xFF1A1F2E)
    val Border = Color(0xFF2A3042)
    val Text = Color(0xFFF2F3F7)
    val TextDim = Color(0xFF9AA3B2)
    val Violet = Color(0xFF7C5CFF)
    val Blue = Color(0xFF5890FF)
    val Green = Color(0xFF2ECC71)
    val Red = Color(0xFFE5484D)
    val Amber = Color(0xFFF5A524)
}

private val NyxScheme = darkColorScheme(
    primary = NyxColors.Violet,
    secondary = NyxColors.Blue,
    background = NyxColors.Background,
    surface = NyxColors.Surface,
    onBackground = NyxColors.Text,
    onSurface = NyxColors.Text,
    error = NyxColors.Red,
)

val NyxCardShape = RoundedCornerShape(16.dp)

@Composable
fun NyxTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NyxScheme, content = content)
}
