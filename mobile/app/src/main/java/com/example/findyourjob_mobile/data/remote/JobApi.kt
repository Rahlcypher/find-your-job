package com.example.findyourjob_mobile.data.remote

import com.example.findyourjob_mobile.data.remote.dto.ApplicationRequest
import com.example.findyourjob_mobile.data.remote.dto.ApplicationResponse
import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import retrofit2.http.*

interface JobApi {
    @GET("api/jobs")
    suspend fun getJobs(
        @Query("location") location: String? = null,
        @Query("jobType") jobType: String? = null,
        @Query("maxDuration") maxDuration: Int? = null
    ): List<JobResponse>

    @GET("api/jobs/{id}")
    suspend fun getJob(@Path("id") id: Long): JobResponse

    @POST("api/jobs/{id}/apply")
    suspend fun applyToJob(
        @Path("id") jobId: Long,
        @Body request: ApplicationRequest
    ): ApplicationResponse

    @GET("api/jobs/my-applications")
    suspend fun getMyApplications(): List<ApplicationResponse>

    @DELETE("api/jobs/applications/{id}")
    suspend fun withdrawApplication(@Path("id") id: Long)
}
