package com.example.findyourjob_mobile.data.remote

import com.example.findyourjob_mobile.BuildConfig
import com.example.findyourjob_mobile.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class JwtAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json
) : Authenticator {

    private var refreshClient: OkHttpClient? = null

    private fun getRefreshClient(): OkHttpClient {
        return refreshClient ?: OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build().also { refreshClient = it }
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization-Refresh") != null) {
            return null
        }

        val refreshToken = runBlocking { tokenManager.getRefreshToken() }
        if (refreshToken == null) {
            runBlocking { tokenManager.clearTokens() }
            return null
        }

        return runBlocking {
            try {
                val requestBody = json.encodeToString(RefreshTokenRequest.serializer(), RefreshTokenRequest(refreshToken))
                    .toRequestBody("application/json".toMediaType())

                val refreshRequest = Request.Builder()
                    .url("${BuildConfig.BASE_URL}api/auth/refresh")
                    .post(requestBody)
                    .header("Authorization-Refresh", "true")
                    .build()

                val refreshResponse = getRefreshClient().newCall(refreshRequest).execute()
                
                if (refreshResponse.isSuccessful) {
                    val authResponse = json.decodeFromString(
                        com.example.findyourjob_mobile.data.remote.dto.AuthResponse.serializer(),
                        refreshResponse.body?.string() ?: throw Exception("Empty body")
                    )
                    tokenManager.saveToken(authResponse.token, authResponse.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${authResponse.token}")
                        .build()
                } else {
                    tokenManager.clearTokens()
                    null
                }
            } catch (e: Exception) {
                tokenManager.clearTokens()
                null
            }
        }
    }
}
