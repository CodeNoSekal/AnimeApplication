package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Yume radii — soft & rounded, but not pill-everything.
 * Mirrors --r-* in tokens/spacing.css.
 */
object YumeRadii {
    val xs: Dp = 6.dp
    val sm: Dp = 12.dp   // posters, thumbnails
    val md: Dp = 14.dp   // buttons, inputs
    val lg: Dp = 18.dp   // cards
    val xl: Dp = 24.dp   // sheets, hero cards
    val xxl: Dp = 30.dp
    val pill: Dp = 999.dp // chips, status pills, avatars
}

/** Material3 Shapes mapped onto the Yume radius scale. */
val YumeShapes = Shapes(
    extraSmall = RoundedCornerShape(YumeRadii.xs),
    small = RoundedCornerShape(YumeRadii.sm),
    medium = RoundedCornerShape(YumeRadii.md),
    large = RoundedCornerShape(YumeRadii.lg),
    extraLarge = RoundedCornerShape(YumeRadii.xl),
)

val PillShape = RoundedCornerShape(YumeRadii.pill)

/**
 * Spacing scale (4px base grid) + layout constants.
 * Mirrors --sp-* and layout vars in tokens/spacing.css.
 */
@Immutable
data class YumeDimens(
    val sp1: Dp = 4.dp,
    val sp2: Dp = 8.dp,
    val sp3: Dp = 12.dp,
    val sp4: Dp = 16.dp,
    val sp5: Dp = 20.dp,
    val sp6: Dp = 24.dp,
    val sp7: Dp = 32.dp,
    val sp8: Dp = 40.dp,
    val sp9: Dp = 48.dp,
    val sp10: Dp = 64.dp,

    val screenPad: Dp = 20.dp,
    val bottomNavHeight: Dp = 84.dp,
    val headerHeight: Dp = 56.dp,
)
