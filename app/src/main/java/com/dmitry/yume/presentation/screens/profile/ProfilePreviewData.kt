package com.dmitry.yume.presentation.screens.profile

import com.dmitry.yume.domain.models.LibraryStatusItem
import com.dmitry.yume.domain.models.Profile
import com.dmitry.yume.domain.models.ProfileLibrary
import com.dmitry.yume.domain.models.ProfileUser
import com.dmitry.yume.domain.models.ProfileWatch
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.edit.EditProfileViewState
import com.dmitry.yume.presentation.screens.profile.edit.UsernameCheck
import com.dmitry.yume.presentation.screens.profile.settings.ProfileSettingsViewState

// Fake addresses: they are not real links, PreviewImages maps them to local drawables.
private const val FRIEREN = "preview://frieren"
// Unknown to PreviewImages, so it renders as a plain gray fill.
private const val NO_AVATAR = "preview://none"

// Numbers match the "5 · Профиль" screen on the design canvas.
object ProfilePreviewData {

    val library = ProfileLibrary(
        total = 148,
        watching = 9,
        planned = 54,
        completed = 79,
        dropped = 6,
        lists = listOf(
            LibraryStatusItem(key = "watching", title = "Смотрю", count = 9),
            LibraryStatusItem(key = "planned", title = "В планах", count = 54),
            LibraryStatusItem(key = "completed", title = "Просмотрено", count = 79),
            LibraryStatusItem(key = "dropped", title = "Брошено", count = 6),
        ),
    )

    val watch = ProfileWatch(
        watchedHours = 705,
        lastWatchedText = "вчера",
    )

    val user = ProfileUser(
        id = 42,
        email = "dmitry@example.com",
        emailVerified = true,
        username = "dmitry",
        displayName = "Дмитрий",
        avatar = FRIEREN,
        isPremium = true,
        premiumUntil = "2027-01-15",
        memberFor = "2 года",
    )

    // Screen 5: verified PRO user with a full library.
    val profile = Profile(
        user = user,
        watch = watch,
        library = library,
    )

    // Screen 6: same user, email not confirmed yet.
    val unverifiedProfile = profile.copy(
        user = user.copy(emailVerified = false),
    )

    // Worst case: just registered, nothing filled in and nothing watched.
    val newProfile = Profile(
        user = ProfileUser(
            id = 43,
            email = "new@example.com",
            emailVerified = true,
            username = "user_43",
            displayName = null,
            avatar = NO_AVATAR,
            isPremium = false,
            premiumUntil = null,
            memberFor = "меньше дня",
        ),
        watch = ProfileWatch(
            watchedHours = 0,
            lastWatchedText = "ещё не смотрели",
        ),
        library = ProfileLibrary(
            total = 0,
            watching = 0,
            planned = 0,
            completed = 0,
            dropped = 0,
            lists = library.lists.map { it.copy(count = 0) },
        ),
    )

    // Worst case: texts that do not fit in one line.
    val longNamesProfile = profile.copy(
        user = user.copy(
            username = "очень_длинный_логин_пользователя",
            displayName = "Очень длинное отображаемое имя пользователя",
            email = "very.long.email.address.for.testing@example.com",
        ),
    )

    // Session user for the edit and settings screens, same person as `user`.
    val sessionUser = User(
        id = user.id,
        email = user.email,
        emailVerified = user.emailVerified,
        username = user.username,
        displayName = "Дмитрий",
        avatarUrl = FRIEREN,
        isAdmin = false,
        isPremium = user.isPremium,
        premiumUntil = user.premiumUntil,
    )

    // Screen 7: form filled from the session user, login is free.
    val editState = EditProfileViewState(
        displayName = sessionUser.displayName,
        username = sessionUser.username,
        usernameCheck = UsernameCheck.Available,
    )

    val editStateChecking = editState.copy(
        username = "dmitry_new",
        usernameCheck = UsernameCheck.Checking,
    )

    val editStateTaken = editState.copy(
        username = "admin",
        usernameCheck = UsernameCheck.Taken,
    )

    val editStateSaving = editState.copy(isSaving = true)

    // Screens 9–12.
    val settingsState = ProfileSettingsViewState()

    val settingsSaving = ProfileSettingsViewState(isSaving = true)

    val settingsError = ProfileSettingsViewState(error = "Неверный пароль")
}
