package com.example.findyourjob_mobile.data.remote

import com.example.findyourjob_mobile.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApi {
    @GET("api/auth/me")
    suspend fun getProfile(): ProfileResponse

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ProfileResponse

    @GET("api/candidate/experiences")
    suspend fun getExperiences(): List<ExperienceResponse>

    @POST("api/candidate/experiences")
    suspend fun addExperience(@Body request: ExperienceRequest): ExperienceResponse

    @DELETE("api/candidate/experiences/{id}")
    suspend fun deleteExperience(@Path("id") id: Long)

    @PUT("api/candidate/experiences/{id}")
    suspend fun updateExperience(@Path("id") id: Long, @Body request: ExperienceRequest): ExperienceResponse

    @GET("api/candidate/educations")
    suspend fun getEducations(): List<EducationResponse>

    @POST("api/candidate/educations")
    suspend fun addEducation(@Body request: EducationRequest): EducationResponse

    @DELETE("api/candidate/educations/{id}")
    suspend fun deleteEducation(@Path("id") id: Long)

    @PUT("api/candidate/educations/{id}")
    suspend fun updateEducation(@Path("id") id: Long, @Body request: EducationRequest): EducationResponse

    @GET("api/candidate/skills")
    suspend fun getSkills(): List<SkillResponse>

    @POST("api/candidate/skills")
    suspend fun addSkill(@Body request: SkillRequest): SkillResponse

    @DELETE("api/candidate/skills/{id}")
    suspend fun deleteSkill(@Path("id") id: Long)

    @PUT("api/candidate/skills/{id}")
    suspend fun updateSkill(@Path("id") id: Long, @Body request: SkillRequest): SkillResponse

    @GET("api/candidate/languages")
    suspend fun getLanguages(): List<LanguageResponse>

    @POST("api/candidate/languages")
    suspend fun addLanguage(@Body request: LanguageRequest): LanguageResponse

    @DELETE("api/candidate/languages/{id}")
    suspend fun deleteLanguage(@Path("id") id: Long)

    @PUT("api/candidate/languages/{id}")
    suspend fun updateLanguage(@Path("id") id: Long, @Body request: LanguageRequest): LanguageResponse

    @GET("api/candidate/cv")
    suspend fun getCv(): CvResponse?

    @Multipart
    @POST("api/candidate/cv")
    suspend fun uploadCv(@Part file: MultipartBody.Part): CvResponse

    @DELETE("api/candidate/cv")
    suspend fun deleteCv()
}
