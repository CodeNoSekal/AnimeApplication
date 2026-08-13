package com.dmitry.yume.presentation.screens.player

import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.repository.OperationResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressSaveQueueTest {

    @Test
    fun `saves progress strictly in submission order`() = runTest {
        val firstStarted = CompletableDeferred<Unit>()
        val finishFirst = CompletableDeferred<Unit>()
        val secondFinished = CompletableDeferred<Unit>()
        val startedPositions = mutableListOf<Long>()
        val completedPositions = mutableListOf<Long>()

        val queue = ProgressSaveQueue(backgroundScope) { progress ->
            startedPositions += progress.positionMs

            if (progress.positionMs == 10_000L) {
                firstStarted.complete(Unit)
                finishFirst.await()
            }

            completedPositions += progress.positionMs

            if (progress.positionMs == 20_000L) {
                secondFinished.complete(Unit)
            }

            OperationResult.Success
        }

        queue.enqueue(progressAt(10_000L))
        firstStarted.await()
        queue.enqueue(progressAt(20_000L))
        runCurrent()

        assertEquals(listOf(10_000L), startedPositions)
        assertFalse(secondFinished.isCompleted)

        finishFirst.complete(Unit)
        secondFinished.await()

        assertEquals(listOf(10_000L, 20_000L), startedPositions)
        assertEquals(listOf(10_000L, 20_000L), completedPositions)
    }

    @Test
    fun `skips only adjacent exact duplicates`() = runTest {
        val lastSaveFinished = CompletableDeferred<Unit>()
        val savedPositions = mutableListOf<Long>()
        val queue = ProgressSaveQueue(backgroundScope) { progress ->
            savedPositions += progress.positionMs

            if (savedPositions.size == 3) {
                lastSaveFinished.complete(Unit)
            }

            OperationResult.Success
        }

        queue.enqueue(progressAt(10_000L))
        queue.enqueue(progressAt(10_000L))
        queue.enqueue(progressAt(20_000L))
        queue.enqueue(progressAt(10_000L))
        lastSaveFinished.await()

        assertEquals(
            listOf(10_000L, 20_000L, 10_000L),
            savedPositions
        )
    }

    @Test
    fun `retries exact progress after failed save`() = runTest {
        val attempts = mutableListOf<Long>()
        val secondAttemptFinished = CompletableDeferred<Unit>()
        val queue = ProgressSaveQueue(backgroundScope) { progress ->
            attempts += progress.positionMs

            if (attempts.size == 2) {
                secondAttemptFinished.complete(Unit)
                OperationResult.Success
            } else {
                OperationResult.Error("network error")
            }
        }

        queue.enqueue(progressAt(10_000L))
        runCurrent()
        queue.enqueue(progressAt(10_000L))
        secondAttemptFinished.await()

        assertEquals(listOf(10_000L, 10_000L), attempts)
    }

    @Test
    fun `close drains already queued progress`() = runTest {
        val saved = CompletableDeferred<Long>()
        val queue = ProgressSaveQueue(backgroundScope) { progress ->
            saved.complete(progress.positionMs)
            OperationResult.Success
        }

        queue.enqueue(progressAt(10_000L))
        queue.close()

        assertEquals(10_000L, saved.await())
    }

    private fun progressAt(positionMs: Long) = Progress(
        animeId = 1,
        episodeNumber = 3,
        positionMs = positionMs,
        durationMs = 30_000L,
        sourceProvider = "libria",
        voiceoverId = null
    )
}
