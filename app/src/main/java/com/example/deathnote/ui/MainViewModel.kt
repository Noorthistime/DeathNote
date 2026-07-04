package com.example.deathnote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deathnote.domain.repository.AuthRepository
import com.example.deathnote.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startRoute = MutableStateFlow<Route?>(null)
    val startRoute: StateFlow<Route?> = _startRoute.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if (user != null) {
                _startRoute.value = Route.Dashboard
            } else {
                _startRoute.value = Route.Onboarding
            }
        }
    }
}
