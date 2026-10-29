package com.washim.washim_launcher.viewmodel

import android.content.Context
import androidx.biometric.BiometricManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LockViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(): LockViewModel =
        LockViewModel(BiometricManager.from(context))

    @Test
    fun isLocked_startsTrue() = runTest(testDispatcher) {
        val vm = buildVm()
        assertTrue(vm.isLocked.value)
    }

    @Test
    fun onScreenOff_setsIsLockedTrue() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onAuthenticated() // unlock first
        vm.onScreenOff()
        assertTrue(vm.isLocked.value)
    }

    @Test
    fun onAuthenticated_setsIsLockedFalse() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onAuthenticated()
        assertFalse(vm.isLocked.value)
    }

    @Test
    fun biometricState_startsIdle() = runTest(testDispatcher) {
        val vm = buildVm()
        assertTrue(vm.biometricState.value == BiometricState.IDLE)
    }

    @Test
    fun checkBiometricAvailability_updatesState() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.checkBiometricAvailability()
        advanceUntilIdle()
        val state = vm.biometricState.value
        assertTrue(
            state == BiometricState.SUCCESS ||
            state == BiometricState.ERROR_NONE_ENROLLED ||
            state == BiometricState.ERROR_OTHER
        )
    }
}
