package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Extra Yume color roles that don't fit Material3's ColorScheme
 * (status colors, rating gold, hairlines, accent states, the soft tint).
 * Exposed through [LocalYumeColors] — read via `YumeTheme.colors`.
 */
@Immutable
data class YumeColors(
    val accent: Color = YuAccent,
    val accentHover: Color = YuAccentHover,
    val accentPress: Color = YuAccentPress,
    val accentSoft: Color = YuAccentSoft,
    val accentLine: Color = YuAccentLine,
    val violet: Color = YuViolet,

    val textPrimary: Color = YuFg1,
    val textSecondary: Color = YuFg2,
    val textMuted: Color = YuFg3,
    val textFaint: Color = YuFg4,

    val surfaceBg: Color = YuInk850,
    val surfaceCard: Color = YuInk800,
    val surfaceRaised: Color = YuInk750,
    val surfaceElevated: Color = YuInk700,

    val rating: Color = YuGold,
    val success: Color = YuSuccess,
    val warning: Color = YuWarning,
    val danger: Color = YuDanger,
    val info: Color = YuInfo,

    val statusWatching: Color = YuStatusWatching,
    val statusPlanned: Color = YuStatusPlanned,
    val statusCompleted: Color = YuStatusCompleted,
    val statusOnHold: Color = YuStatusOnHold,
    val statusDropped: Color = YuStatusDropped,

    val line: Color = YuLine,
    val lineStrong: Color = YuLineStrong,
    val scrim: Color = YuScrim,
)
