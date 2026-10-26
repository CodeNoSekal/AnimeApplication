package com.dmitry.yume.presentation.screens.player.screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.SystemClock
import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class PendingOrientation {
    PORTRAIT,
    LANDSCAPE,
}

private const val ORIENTATION_CONFIRMATION_MS = 600L

@Stable
class PlaybackOrientationController internal constructor(
    private val activity: Activity,
) {
    private var pendingOrientation by mutableStateOf<PendingOrientation?>(null)
    private var matchingOrientationSince: Long? = null

    fun requestLandscape() {
        pendingOrientation = PendingOrientation.LANDSCAPE
        matchingOrientationSince = null
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    fun requestPortrait() {
        pendingOrientation = PendingOrientation.PORTRAIT
        matchingOrientationSince = null
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    internal fun onPhysicalOrientationChanged(orientation: Int) {
        val target = pendingOrientation ?: return
        val confirmed = when (target) {
            PendingOrientation.PORTRAIT -> orientation.isPortraitOrientation()
            PendingOrientation.LANDSCAPE -> orientation.isLandscapeOrientation()
        }

        if (!confirmed) {
            matchingOrientationSince = null
            return
        }

        val now = SystemClock.uptimeMillis()
        val matchingSince = matchingOrientationSince

        if (matchingSince == null) {
            matchingOrientationSince = now
        } else if (now - matchingSince >= ORIENTATION_CONFIRMATION_MS) {
            pendingOrientation = null
            matchingOrientationSince = null
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }
}

@Composable
fun rememberPlaybackOrientationController(
    activity: Activity,
): PlaybackOrientationController {
    val controller = remember(activity) {
        PlaybackOrientationController(activity)
    }

    DisposableEffect(activity, controller) {
        val originalOrientation = activity.requestedOrientation
        val listener = object : OrientationEventListener(activity) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation != ORIENTATION_UNKNOWN) {
                    controller.onPhysicalOrientationChanged(orientation)
                }
            }
        }

        if (listener.canDetectOrientation()) {
            listener.enable()
        }

        onDispose {
            listener.disable()
            activity.requestedOrientation = originalOrientation
        }
    }

    return controller
}

private fun Int.isPortraitOrientation(): Boolean =
    this in 0..30 || this in 150..210 || this in 330..359

private fun Int.isLandscapeOrientation(): Boolean =
    this in 60..120 || this in 240..300
