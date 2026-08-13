package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.repository.OperationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

internal class ProgressSaveQueue(
    scope: CoroutineScope,
    private val save: suspend (Progress) -> OperationResult
) {
    private val queue = Channel<Progress>(capacity = Channel.UNLIMITED)

    init {
        scope.launch {
            var lastSaved: Progress? = null

            for (progress in queue) {
                if (progress == lastSaved) continue

                try {
                    if (save(progress) is OperationResult.Success) {
                        lastSaved = progress
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    // A failed request must not stop processing later progress updates.
                }
            }
        }
    }

    fun enqueue(progress: Progress) {
        queue.trySend(progress)
    }

    fun close() {
        queue.close()
    }
}
