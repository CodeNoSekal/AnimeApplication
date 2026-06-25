package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Yume is a dark-only design system. This is the single colour source of truth
 * for Material3 components; the extras (status colors, gold, hairlines) live in
 * [YumeColors] and [YumeDimens], exposed via CompositionLocals.
 */
private val YumeDarkColorScheme = darkColorScheme(
    primary = YuAccent,
    onPrimary = YuOnAccent,
    primaryContainer = YuAccentPress,
    onPrimaryContainer = YuFg1,
    secondary = YuViolet,
    onSecondary = YuOnAccent,
    background = YuInk850,
    onBackground = YuFg1,
    surface = YuInk800,
    onSurface = YuFg1,
    surfaceVariant = YuInk700,
    onSurfaceVariant = YuFg2,
    outline = YuLineStrong,
    outlineVariant = YuLine,
    error = YuDanger,
    onError = YuOnAccent,
    scrim = YuScrim,
)

val LocalYumeColors = staticCompositionLocalOf { YumeColors() }
val LocalYumeDimens = staticCompositionLocalOf { YumeDimens() }

/** Wrap your app in this. `YumeTheme { ... }` */
@Composable
fun YumeTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalYumeColors provides YumeColors(),
        LocalYumeDimens provides YumeDimens(),
    ) {
        MaterialTheme(
            colorScheme = YumeDarkColorScheme,
            typography = YumeTypography,
            shapes = YumeShapes,
            content = content,
        )
    }
}

/** Convenience accessors: `YumeTheme.colors.statusWatching`, `YumeTheme.dimens.sp4`. */
object YumeTheme {
    val colors: YumeColors
        @Composable @ReadOnlyComposable get() = LocalYumeColors.current
    val dimens: YumeDimens
        @Composable @ReadOnlyComposable get() = LocalYumeDimens.current
}
