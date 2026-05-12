package com.example.findyourjob_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findyourjob_mobile.data.repository.AuthRepository
import com.example.findyourjob_mobile.data.repository.Result
import com.example.findyourjob_mobile.domain.usecase.LoginUseCase
import com.example.findyourjob_mobile.domain.usecase.LogoutUseCase
import com.example.findyourjob_mobile.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val isLoggedIn = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userRole = authRepository.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, roles: List<String> = listOf("CANDIDATE")) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = registerUseCase(email, password, firstName, lastName, roles)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = AuthState()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
