package com.example.findyourjob_mobile.data.repository

import com.example.findyourjob_mobile.data.remote.ProfileApi
import com.example.findyourjob_mobile.data.remote.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi
) {
    suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            Result.Success(profileApi.getProfile())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<ProfileResponse> {
        return try {
            Result.Success(profileApi.updateProfile(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getExperiences(): Result<List<ExperienceResponse>> {
        return try {
            Result.Success(profileApi.getExperiences())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun addExperience(request: ExperienceRequest): Result<ExperienceResponse> {
        return try {
            Result.Success(profileApi.addExperience(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteExperience(id: Long): Result<Unit> {
        return try {
            profileApi.deleteExperience(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateExperience(id: Long, request: ExperienceRequest): Result<ExperienceResponse> {
        return try {
            Result.Success(profileApi.updateExperience(id, request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getEducations(): Result<List<EducationResponse>> {
        return try {
            Result.Success(profileApi.getEducations())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun addEducation(request: EducationRequest): Result<EducationResponse> {
        return try {
            Result.Success(profileApi.addEducation(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteEducation(id: Long): Result<Unit> {
        return try {
            profileApi.deleteEducation(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateEducation(id: Long, request: EducationRequest): Result<EducationResponse> {
        return try {
            Result.Success(profileApi.updateEducation(id, request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getSkills(): Result<List<SkillResponse>> {
        return try {
            Result.Success(profileApi.getSkills())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun addSkill(request: SkillRequest): Result<SkillResponse> {
        return try {
            Result.Success(profileApi.addSkill(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteSkill(id: Long): Result<Unit> {
        return try {
            profileApi.deleteSkill(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateSkill(id: Long, request: SkillRequest): Result<SkillResponse> {
        return try {
            Result.Success(profileApi.updateSkill(id, request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getLanguages(): Result<List<LanguageResponse>> {
        return try {
            Result.Success(profileApi.getLanguages())
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun addLanguage(request: LanguageRequest): Result<LanguageResponse> {
        return try {
            Result.Success(profileApi.addLanguage(request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteLanguage(id: Long): Result<Unit> {
        return try {
            profileApi.deleteLanguage(id)
            Result.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun updateLanguage(id: Long, request: LanguageRequest): Result<LanguageResponse> {
        return try {
            Result.Success(profileApi.updateLanguage(id, request))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun getCv(): Result<CvResponse?> {
        return try {
            Result.Success(profileApi.getCv())
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Result.Success(null)
            } else {
                Result.Error(parseErrorMessage(e), e.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun uploadCv(file: File): Result<CvResponse> {
        return try {
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            Result.Success(profileApi.uploadCv(body))
        } catch (e: retrofit2.HttpException) {
            Result.Error(parseErrorMessage(e), e.code())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur réseau")
        }
    }

    suspend fun deleteCv(): Result<Unit> {
        return try {
            profileApi.deleteCv()
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
