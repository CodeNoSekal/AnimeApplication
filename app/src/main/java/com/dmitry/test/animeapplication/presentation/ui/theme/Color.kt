package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Yume color tokens — Soft Dark, blue-violet.
 * Mirrors tokens/colors.css from the Yume design system (1:1 hex values).
 */

// ---- Base neutrals (deep blue-violet near-black) ----
val YuInk900 = Color(0xFF06070E) // deepest / scrim
val YuInk850 = Color(0xFF0A0B16) // app background
val YuInk800 = Color(0xFF11121F) // surface
val YuInk750 = Color(0xFF171A2A) // surface raised
val YuInk700 = Color(0xFF1E2236) // elevated / pressed
val YuInk600 = Color(0xFF2A2F49) // hairline-strong / border
val YuInk500 = Color(0xFF383E5E) // disabled fill

// ---- Text ramp ----
val YuFg1 = Color(0xFFF3F4FB) // primary text
val YuFg2 = Color(0xFFA2A5C2) // secondary text
val YuFg3 = Color(0xFF71748F) // tertiary / muted
val YuFg4 = Color(0xFF4B4F6E) // faint / placeholder

// ---- Accent (blue-violet / periwinkle) ----
val YuAccent = Color(0xFF6E6CFF)
val YuAccentHover = Color(0xFF8A88FF)
val YuAccentPress = Color(0xFF5755E6)
val YuAccentSoft = Color(0x296E6CFF)  // 16% alpha
val YuAccentLine = Color(0x576E6CFF)  // 34% alpha
val YuOnAccent = Color(0xFFFFFFFF)

// ---- Secondary violet (gradients, depth) ----
val YuViolet = Color(0xFFA78BFA)
val YuVioletSoft = Color(0x29A78BFA)

// ---- Rating gold (stars) ----
val YuGold = Color(0xFFF5C45E)
val YuGoldSoft = Color(0x29F5C45E)

// ---- Semantic ----
val YuSuccess = Color(0xFF3FC9A0)
val YuWarning = Color(0xFFF5B53D)
val YuDanger = Color(0xFFF4615E)
val YuInfo = Color(0xFF5C9DFF)

// ---- Watch-list status ----
val YuStatusWatching = Color(0xFF3FC9A0)  // Смотрю
val YuStatusPlanned = Color(0xFF5C9DFF)   // В планах
val YuStatusCompleted = Color(0xFFA78BFA) // Просмотрено
val YuStatusOnHold = Color(0xFFF5B53D)    // Отложено
val YuStatusDropped = Color(0xFFF4615E)   // Брошено

// ---- Hairlines & overlays ----
val YuLine = Color(0x12FFFFFF)        // 7% white
val YuLineStrong = Color(0x21FFFFFF)  // 13% white
val YuScrim = Color(0xBD06070E)       // 74% ink-900
