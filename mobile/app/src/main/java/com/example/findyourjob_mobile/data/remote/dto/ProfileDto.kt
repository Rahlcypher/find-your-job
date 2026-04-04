package com.example.findyourjob_mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Long,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val roles: List<String> = emptyList()
)

@Serializable
data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val location: String? = null
)

@Serializable
data class ExperienceResponse(
    val id: Long,
    val title: String? = null,
    val company: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val currentJob: Boolean = false
)

@Serializable
data class ExperienceRequest(
    val title: String,
    val company: String,
    val description: String? = null,
    val startDate: String,
    val endDate: String? = null,
    val currentJob: Boolean = false
)

@Serializable
data class EducationResponse(
    val id: Long,
    val degree: String? = null,
    val school: String? = null,
    val fieldOfStudy: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)

@Serializable
data class EducationRequest(
    val degree: String,
    val school: String,
    val fieldOfStudy: String? = null,
    val startDate: String,
    val endDate: String? = null
)

@Serializable
data class SkillResponse(
    val id: Long,
    val name: String? = null,
    val level: String? = null
)

@Serializable
data class SkillRequest(
    val name: String,
    val level: String? = null
)

@Serializable
data class LanguageResponse(
    val id: Long,
    val name: String? = null,
    val level: String? = null
)

@Serializable
data class LanguageRequest(
    val name: String,
    val level: String? = null
)

@Serializable
data class CvResponse(
    val id: Long,
    val fileName: String? = null,
    val fileType: String? = null,
    val fileSize: Long? = null,
    val uploadedAt: String? = null
)
