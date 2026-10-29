package com.washim.washim_launcher.viewmodel

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class BiometricState { IDLE, PROMPTING, SUCCESS, ERROR_NONE_ENROLLED, ERROR_OTHER }

@HiltViewModel
class LockViewModel @Inject constructor(
    private val biometricManager: BiometricManager,
) : ViewModel() {

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _biometricState = MutableStateFlow(BiometricState.IDLE)
    val biometricState: StateFlow<BiometricState> = _biometricState.asStateFlow()

    /** Called when the screen turns off — re-locks the device. */
    fun onScreenOff() {
        _isLocked.value = true
    }

    /** Called when the user has successfully authenticated. */
    fun onAuthenticated() {
        _isLocked.value = false
        _biometricState.value = BiometricState.SUCCESS
    }

    /** Checks biometric availability and updates [biometricState]. */
    fun checkBiometricAvailability() {
        val result = biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        _biometricState.value = when (result) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricState.SUCCESS
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricState.ERROR_NONE_ENROLLED
            else -> BiometricState.ERROR_OTHER
        }
    }
}
