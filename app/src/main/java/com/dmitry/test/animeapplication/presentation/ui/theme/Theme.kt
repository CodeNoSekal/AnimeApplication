package com.dmitry.test.animeapplication.presentation.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,                // акцентные кнопки, активные элементы
    onPrimary = Color(0xFF15131A),          // тёмный текст/иконки на акценте
    primaryContainer = AccentDark,          // насыщенная акцентная подложка
    onPrimaryContainer = TextPrimary,

    secondary = AccentLight,                // вторичный акцент (ховер/выделение)
    onSecondary = Color(0xFF15131A),

    tertiary = AccentLight,

    background = BackgroundMain,             // общий фон
    onBackground = TextPrimary,

    surface = SurfaceCard,                  // карточки, нижняя панель
    onSurface = TextPrimary,
    surfaceVariant = SurfaceInput,          // поля ввода, чипы
    onSurfaceVariant = TextSecondary,

    outline = Divider,                      // разделители, обводки
    outlineVariant = Divider,

    error = Danger,
    onError = Color(0xFF2A0A0A),
)


@Composable
fun AnimeApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false   // белые иконки статус-бара
            controller.isAppearanceLightNavigationBars = false // белые иконки навбара
        }
    }
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}