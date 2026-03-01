package com.example.findyourjob_mobile.domain.usecase

import com.example.findyourjob_mobile.data.remote.dto.AuthResponse
import com.example.findyourjob_mobile.data.repository.AuthRepository
import com.example.findyourjob_mobile.data.repository.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        if (email.isBlank()) return Result.Error("Email requis")
        if (password.isBlank()) return Result.Error("Mot de passe requis")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error("Email invalide")
        }
        return authRepository.login(email, password)
    }
}
