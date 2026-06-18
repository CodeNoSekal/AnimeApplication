package com.dmitry.test.animeapplication.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// AnimeApp — тёмная тема с нейтральным серым акцентом
// Значения соответствуют design-spec.md (раздел 1.1)

// --- Фоны / поверхности ---
val BackgroundMain = Color(0xFF1A1A1A)  // фон всего приложения (почти чёрный)
val SurfaceCard = Color(0xFF222222)     // карточки, нижняя панель
val SurfaceInput = Color(0xFF2B2B2B)    // поля ввода, чипы, вторичные блоки
val ModalScrim = Color(0xF21A1A1A)      // подложка модалок/боттомшитов (~95%)

// --- Акцент (серый) ---
val AccentPrimary = Color(0xFFBDBDBD)   // основной акцент: кнопки, активные иконки
val AccentLight = Color(0xFFDADADA)     // ховер/нажатие (светлее)
val AccentDark = Color(0xFF767676)      // градиенты, активные подложки (темнее)
val AccentShadow = Color(0x40BDBDBD)    // свечение/тень под акцентом (25% alpha)

// --- Текст ---
val TextPrimary = Color(0xFFF5F5F5)     // заголовки, основной текст
val TextSecondary = Color(0xFFB3B3B3)   // подписи, метаданные
val TextPlaceholder = Color(0xFF7E7E7E) // плейсхолдеры, неактивное

// --- Прочее ---
val Divider = Color(0xFF333333)         // разделители, обводки (использовать редко)
val Success = Color(0xFF34D399)         // «просмотрено», онлайн-статус
val Warning = Color(0xFFFBBF24)         // рейтинг (звёзды), бейджи
val Danger = Color(0xFFF87171)          // ошибки, удаление, выход