package com.app.whakaara.logic

import android.app.Application
import android.app.NotificationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.NotificationCompat
import app.cash.turbine.test
import com.app.whakaara.MainDispatcherRule
import com.app.whakaara.data.datastore.PreferencesDataStore
import com.app.whakaara.state.Lap
import com.app.whakaara.state.StopwatchState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class StopwatchManagerTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var stopwatchManagerWrapper: StopwatchManagerWrapper
    private lateinit var app: Application
    private lateinit var notificationManager: NotificationManager
    private lateinit var stopwatchNotificationBuilder: NotificationCompat.Builder
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var preferencesDatastore: PreferencesDataStore
    private val testDispatcher = StandardTestDispatcher()
    private val managedCoroutineScope = TestScope(testDispatcher)
    private val id = 400

    @Before
    fun setUp() {
        app = mockk()
        notificationManager = mockk()
        stopwatchNotificationBuilder = mockk()
        coroutineScope = mockk()
        preferencesDatastore = mockk()
        stopwatchManagerWrapper = StopwatchManagerWrapper(app, notificationManager, stopwatchNotificationBuilder, managedCoroutineScope, preferencesDatastore)
    }

    @Test
    fun `pause stopwatch`() = runTest {
        // Given
        val notificationId = slot<Int>()
        coEvery { notificationManager.cancel(any()) } just Runs

        // When
        stopwatchManagerWrapper.pauseStopwatch()

        // Then
        verify(exactly = 1) { notificationManager.cancel(capture(notificationId)) }
        assertEquals(id, notificationId.captured)
        stopwatchManagerWrapper.stopwatchState.test {
            val state = awaitItem()
            with(state) {
                assertEquals(false, isActive)
                assertEquals(true, isPaused)
            }
        }
    }

    @Test
    fun `reset stopwatch`() = runTest {
        // Given
        val notificationId = slot<Int>()
        coEvery { notificationManager.cancel(any()) } just Runs

        // When
        stopwatchManagerWrapper.resetStopwatch()

        // Then
        verify(exactly = 1) { notificationManager.cancel(capture(notificationId)) }
        assertEquals(id, notificationId.captured)
        stopwatchManagerWrapper.stopwatchState.test {
            val state = awaitItem()
            with(state) {
                assertEquals(0L, timeMillis)
                assertEquals(0L, lastTimeStamp)
                assertEquals("00:00:00:000", formattedTime)
                assertEquals(false, isActive)
                assertEquals(true, isStart)
                assertEquals(false, isPaused)
                assert(lapList.isEmpty())
            }
        }
    }

    @Test
    fun `lap stopwatch empty list`() = runTest {
        // Given
        val current = 123123L
        stopwatchManagerWrapper.stopwatchState.value = StopwatchState(
            timeMillis = current
        )

        // When
        stopwatchManagerWrapper.lapStopwatch()

        // Then
        stopwatchManagerWrapper.stopwatchState.test {
            val state = awaitItem()
            with(state) {
                assert(lapList.isNotEmpty())
                assertEquals(1, lapList.size)
                assertEquals(current, lapList.first().diff)
                assertEquals(current, lapList.first().time)
            }
        }
    }

    @Test
    fun `lap stopwatch not empty list`() = runTest {
        // Given
        val current = 123123L
        stopwatchManagerWrapper.stopwatchState.value = StopwatchState(
            timeMillis = current,
            lapList = mutableListOf(
                Lap(
                    time = 123L,
                    diff = 123L
                ),
                Lap(
                    time = 456L,
                    diff = 456L
                )
            )
        )

        // When
        stopwatchManagerWrapper.lapStopwatch()

        // Then
        stopwatchManagerWrapper.stopwatchState.test {
            val state = awaitItem()
            with(state) {
                assert(lapList.isNotEmpty())
                assertEquals(3, lapList.size)
                assertEquals(123123, lapList.last().time)
                assertEquals(122667, lapList.last().diff)
            }
        }
    }

    @Test
    fun `recreate stopwatch paused`() = runTest {
        // Given
        val pausedState = StopwatchState(
            isPaused = true,
            isActive = false,
            isStart = false,
            timeMillis = 112233L,
            formattedTime = "11:22:33:444",
            lapList = mutableListOf(
                Lap(
                    time = 420L,
                    diff = 420L
                )
            )
        )

        // When
        stopwatchManagerWrapper.recreateStopwatchPaused(state = pausedState)

        // Then
        stopwatchManagerWrapper.stopwatchState.test {
            val state = awaitItem()
            with(state) {
                assertEquals(true, isPaused)
                assertEquals(false, isActive)
                assertEquals(false, isStart)
                assertEquals(112233L, timeMillis)
                assertEquals("11:22:33:444", formattedTime)
                assertEquals(1, lapList.size)
                assertEquals(420L, lapList.first().time)
                assertEquals(420L, lapList.first().diff)
            }
        }
    }

    @Test
    fun `cancel notification`() = runTest {
        // Given
        val notificationId = slot<Int>()
        coEvery { notificationManager.cancel(any()) } just Runs

        // When
        stopwatchManagerWrapper.cancelNotification()

        // Then
        verify(exactly = 1) { notificationManager.cancel(capture(notificationId)) }
        assertEquals(id, notificationId.captured)
    }
}
