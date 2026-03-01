package com.example.findyourjob_mobile.domain.usecase

import com.example.findyourjob_mobile.data.remote.dto.AuthResponse
import com.example.findyourjob_mobile.data.repository.AuthRepository
import com.example.findyourjob_mobile.data.repository.Result
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roles: List<String> = listOf("CANDIDATE")
    ): Result<AuthResponse> {
        if (email.isBlank()) return Result.Error("Email requis")
        if (password.isBlank()) return Result.Error("Mot de passe requis")
        if (firstName.isBlank()) return Result.Error("Prénom requis")
        if (lastName.isBlank()) return Result.Error("Nom requis")
        if (password.length < 6) return Result.Error("Mot de passe trop court")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error("Email invalide")
        }
        return authRepository.register(email, password, firstName, lastName, roles)
    }
}
