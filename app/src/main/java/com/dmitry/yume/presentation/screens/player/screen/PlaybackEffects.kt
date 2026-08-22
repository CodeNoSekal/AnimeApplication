package com.dmitry.yume.presentation.screens.player.screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.SystemClock
import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dmitry.yume.presentation.screens.player.PlaybackController
import kotlinx.coroutines.delay

private const val PROGRESS_SAVE_INTERVAL_MS = 20_000L

@Composable
fun PlaybackLifecycleEffect(
    controller: PlaybackController
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                controller.saveCurrentProgress()
                controller.player.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun PlaybackPeriodicProgressEffect(
    controller: PlaybackController,
) {
    LaunchedEffect(controller) {
        while (true) {
            delay(PROGRESS_SAVE_INTERVAL_MS)

            if (controller.player.isPlaying) {
                controller.saveCurrentProgress()
            }
        }
    }
}

@Composable
fun PlaybackReleaseEffect(
    controller: PlaybackController
) {
    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }
}

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

@Composable
fun PlaybackSystemUiEffect(
    activity: Activity,
    isLandscape: Boolean
) {
    DisposableEffect(activity, isLandscape) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(
            window,
            window.decorView
        )

        if (isLandscape) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
