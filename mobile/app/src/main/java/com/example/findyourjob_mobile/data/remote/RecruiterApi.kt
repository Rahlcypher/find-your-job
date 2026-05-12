package com.example.findyourjob_mobile.data.remote

import com.example.findyourjob_mobile.data.remote.dto.ApplicationResponse
import com.example.findyourjob_mobile.data.remote.dto.JobRequest
import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import com.example.findyourjob_mobile.data.remote.dto.StatusUpdateRequest
import retrofit2.http.*

interface RecruiterApi {
    @GET("api/recruiter/jobs")
    suspend fun getMyJobs(): List<JobResponse>

    @POST("api/recruiter/jobs")
    suspend fun createJob(@Body request: JobRequest): JobResponse

    @PUT("api/recruiter/jobs/{id}")
    suspend fun updateJob(@Path("id") id: Long, @Body request: JobRequest): JobResponse

    @DELETE("api/recruiter/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long)

    @GET("api/recruiter/jobs/{id}/applications")
    suspend fun getApplicationsForJob(@Path("id") jobId: Long): List<ApplicationResponse>

    @GET("api/recruiter/applications")
    suspend fun getAllMyApplications(): List<ApplicationResponse>

    @PUT("api/recruiter/applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") applicationId: Long,
        @Body request: StatusUpdateRequest
    ): ApplicationResponse
}
