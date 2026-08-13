package com.dmitry.yume.presentation.screens.player

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PlayerLifecycleEffect(
    controller: PlayerController
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
fun PlayerReleaseEffect(
    controller: PlayerController,
    activity: Activity
) {
    DisposableEffect(controller, activity) {
        val originalOrientation = activity.requestedOrientation

        onDispose {
            controller.release()
            activity.requestedOrientation = originalOrientation
        }
    }
}

@Composable
fun PlayerSystemUiEffect(
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