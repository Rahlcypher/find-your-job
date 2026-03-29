package com.example.findyourjob_mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JobResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val company: String? = null,
    val location: String? = null,
    val salaryMin: Int? = null,
    val salaryMax: Int? = null,
    val jobType: String? = null,
    val workSchedule: String? = null,
    val remotePolicy: String? = null,
    val duration: Int? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
    val expiresAt: String? = null,
    val recruiterId: Long? = null,
    val recruiterName: String? = null
)

@Serializable
data class ApplicationRequest(
    val jobId: Long,
    val coverLetter: String? = null
)

@Serializable
data class ApplicationResponse(
    val id: Long,
    val status: String? = null,
    val coverLetter: String? = null,
    val appliedAt: String? = null
)
