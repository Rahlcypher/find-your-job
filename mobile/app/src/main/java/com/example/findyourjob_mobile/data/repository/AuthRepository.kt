package com.example.findyourjob_mobile.data.repository

import com.example.findyourjob_mobile.data.remote.AuthApi
import com.example.findyourjob_mobile.data.remote.TokenManager
import com.example.findyourjob_mobile.data.remote.dto.AuthResponse
import com.example.findyourjob_mobile.data.remote.dto.LoginRequest
import com.example.findyourjob_mobile.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    val isLoggedIn: Flow<Boolean> = tokenManager.token.map { it != null }

    private fun parseErrorMessage(e: retrofit2.HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                val json = Json { ignoreUnknownKeys = true }
                val errorResponse = json.decodeFromString<Map<String, String>>(errorBody)
                errorResponse["error"] ?: "Erreur ${e.code()}"
            } else {
                "Erreur ${e.code()}"
            }
        } catch (ex: Exception) {
            "Erreur ${e.code()}"
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            tokenManager.saveToken(response.token, response.refreshToken)
            Result.Success(response)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roles: List<String> = listOf("ROLE_CANDIDATE")
    ): Result<AuthResponse> {
        return try {
            val response = authApi.register(
                RegisterRequest(email, password, firstName, lastName, roles)
            )
            tokenManager.saveToken(response.token, response.refreshToken)
            Result.Success(response)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }
}
