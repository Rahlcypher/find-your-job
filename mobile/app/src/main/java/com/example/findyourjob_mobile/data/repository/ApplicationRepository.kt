package com.example.findyourjob_mobile.data.repository

import com.example.findyourjob_mobile.data.remote.JobApi
import com.example.findyourjob_mobile.data.remote.dto.ApplicationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationRepository @Inject constructor(
    private val jobApi: JobApi
) {
    suspend fun getMyApplications(): Result<List<ApplicationResponse>> {
        return try {
            Result.Success(jobApi.getMyApplications())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun withdrawApplication(id: Long): Result<Unit> {
        return try {
            jobApi.withdrawApplication(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    private fun parseErrorMessage(e: retrofit2.HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                val errorResponse = json.decodeFromString<Map<String, String>>(errorBody)
                errorResponse["error"] ?: "Erreur ${e.code()}"
            } else {
                "Erreur ${e.code()}"
            }
        } catch (ex: Exception) {
            "Erreur ${e.code()}"
        }
    }
}
