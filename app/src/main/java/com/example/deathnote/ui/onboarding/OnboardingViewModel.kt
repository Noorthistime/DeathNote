package com.example.deathnote.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deathnote.domain.repository.AuthRepository
import com.example.deathnote.data.remote.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
    val googleAuthHelper: GoogleAuthHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onGoogleSignInSuccess(idToken: String) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                // Trigger sync immediately after sign in
                syncRepository.syncData()
                _uiState.value = OnboardingUiState.Success
            } else {
                _uiState.value = OnboardingUiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}
