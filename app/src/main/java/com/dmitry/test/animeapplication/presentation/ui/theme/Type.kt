package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dmitry.test.animeapplication.R

/**
 * Yume type — Nunito (soft, rounded sans) everywhere. No serif.
 * Mirrors tokens/typography.css.
 *
 * SETUP: download Nunito from Google Fonts (https://fonts.google.com/specimen/Nunito)
 * and drop the .ttf files into res/font/ with these names, OR use the Downloadable
 * Fonts provider (see android/README.md). Weights used: 400/500/600/700/800/900.
 */
val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black),
)

/**
 * Named Yume text styles (display → overline). Use directly, e.g.
 * `Text("Каталог", style = YumeType.display)`.
 */
object YumeType {
    val displayLg = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 36.sp, letterSpacing = (-0.02).em)
    val display   = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 30.sp, letterSpacing = (-0.02).em)
    val h1        = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 27.sp, letterSpacing = (-0.01).em)
    val h2        = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
    val h3        = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 21.sp)
    val body      = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp)
    val bodyMedium= TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 23.sp)
    val sm        = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 19.sp)
    val xs        = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 17.sp)
    val overline  = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.10.em)
    /** Numbers: ratings, episodes, timecodes — tabular feel via Nunito bold. */
    val mono      = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp)
}

/** Material3 Typography mapped onto the Yume scale (for Material components). */
val YumeTypography = Typography(
    displayLarge = YumeType.displayLg,
    displaySmall = YumeType.display,
    headlineLarge = YumeType.h1,
    headlineMedium = YumeType.h2,
    titleLarge = YumeType.h3,
    bodyLarge = YumeType.body,
    bodyMedium = YumeType.sm,
    labelLarge = YumeType.bodyMedium,
    labelSmall = YumeType.overline,
)
