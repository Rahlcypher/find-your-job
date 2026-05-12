package com.example.findyourjob_mobile.data.repository

import com.example.findyourjob_mobile.data.remote.RecruiterApi
import com.example.findyourjob_mobile.data.remote.dto.ApplicationResponse
import com.example.findyourjob_mobile.data.remote.dto.JobRequest
import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import com.example.findyourjob_mobile.data.remote.dto.StatusUpdateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecruiterRepository @Inject constructor(
    private val recruiterApi: RecruiterApi
) {
    suspend fun getMyJobs(): Result<List<JobResponse>> {
        return try {
            Result.Success(recruiterApi.getMyJobs())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun createJob(request: JobRequest): Result<JobResponse> {
        return try {
            Result.Success(recruiterApi.createJob(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateJob(id: Long, request: JobRequest): Result<JobResponse> {
        return try {
            Result.Success(recruiterApi.updateJob(id, request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteJob(id: Long): Result<Unit> {
        return try {
            recruiterApi.deleteJob(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getApplicationsForJob(jobId: Long): Result<List<ApplicationResponse>> {
        return try {
            Result.Success(recruiterApi.getApplicationsForJob(jobId))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getAllMyApplications(): Result<List<ApplicationResponse>> {
        return try {
            Result.Success(recruiterApi.getAllMyApplications())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateApplicationStatus(applicationId: Long, status: String): Result<ApplicationResponse> {
        return try {
            Result.Success(recruiterApi.updateApplicationStatus(applicationId, StatusUpdateRequest(status)))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    private fun parseErrorMessage(e: retrofit2.HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            errorBody ?: "Erreur ${e.code()}"
        } catch (ex: Exception) {
            "Erreur ${e.code()}"
        }
    }
}
