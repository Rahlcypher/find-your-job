package com.example.findyourjob_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findyourjob_mobile.data.remote.dto.*
import com.example.findyourjob_mobile.data.repository.ProfileRepository
import com.example.findyourjob_mobile.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val profile: ProfileResponse? = null,
    val experiences: List<ExperienceResponse> = emptyList(),
    val educations: List<EducationResponse> = emptyList(),
    val skills: List<SkillResponse> = emptyList(),
    val languages: List<LanguageResponse> = emptyList(),
    val cv: CvResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = profileRepository.getProfile()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        profile = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }

            loadExperiences()
            loadEducations()
            loadSkills()
            loadLanguages()
            loadCv()
        }
    }

    private fun loadExperiences() {
        viewModelScope.launch {
            when (val result = profileRepository.getExperiences()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(experiences = result.data)
                }
                is Result.Error -> {}
            }
        }
    }

    private fun loadEducations() {
        viewModelScope.launch {
            when (val result = profileRepository.getEducations()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(educations = result.data)
                }
                is Result.Error -> {}
            }
        }
    }

    private fun loadSkills() {
        viewModelScope.launch {
            when (val result = profileRepository.getSkills()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(skills = result.data)
                }
                is Result.Error -> {}
            }
        }
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            when (val result = profileRepository.getLanguages()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(languages = result.data)
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateProfile(firstName: String?, lastName: String?, phone: String?, location: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = UpdateProfileRequest(firstName, lastName, phone, location)
            when (val result = profileRepository.updateProfile(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        profile = result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun deleteExperience(id: Long) {
        viewModelScope.launch {
            when (profileRepository.deleteExperience(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        experiences = _state.value.experiences.filter { it.id != id }
                    )
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateExperience(id: Long, title: String, company: String, description: String?, startDate: String, endDate: String?, currentJob: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = ExperienceRequest(title, company, description, startDate, endDate, currentJob)
            when (val result = profileRepository.updateExperience(id, request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        experiences = _state.value.experiences.map { if (it.id == id) result.data else it },
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun deleteEducation(id: Long) {
        viewModelScope.launch {
            when (profileRepository.deleteEducation(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        educations = _state.value.educations.filter { it.id != id }
                    )
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateEducation(id: Long, degree: String, school: String, fieldOfStudy: String?, startDate: String, endDate: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = EducationRequest(degree, school, fieldOfStudy, startDate, endDate)
            when (val result = profileRepository.updateEducation(id, request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        educations = _state.value.educations.map { if (it.id == id) result.data else it },
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun deleteSkill(id: Long) {
        viewModelScope.launch {
            when (profileRepository.deleteSkill(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        skills = _state.value.skills.filter { it.id != id }
                    )
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateSkill(id: Long, name: String, level: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = SkillRequest(name, level)
            when (val result = profileRepository.updateSkill(id, request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        skills = _state.value.skills.map { if (it.id == id) result.data else it },
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun deleteLanguage(id: Long) {
        viewModelScope.launch {
            when (profileRepository.deleteLanguage(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        languages = _state.value.languages.filter { it.id != id }
                    )
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateLanguage(id: Long, name: String, level: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = LanguageRequest(name, level)
            when (val result = profileRepository.updateLanguage(id, request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        languages = _state.value.languages.map { if (it.id == id) result.data else it },
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun addExperience(title: String, company: String, description: String?, startDate: String, endDate: String?, currentJob: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = ExperienceRequest(title, company, description, startDate, endDate, currentJob)
            when (val result = profileRepository.addExperience(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        experiences = _state.value.experiences + result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun addEducation(degree: String, school: String, fieldOfStudy: String?, startDate: String, endDate: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = EducationRequest(degree, school, fieldOfStudy, startDate, endDate)
            when (val result = profileRepository.addEducation(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        educations = _state.value.educations + result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun addSkill(name: String, level: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = SkillRequest(name, level)
            when (val result = profileRepository.addSkill(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        skills = _state.value.skills + result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun addLanguage(name: String, level: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = LanguageRequest(name, level)
            when (val result = profileRepository.addLanguage(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        languages = _state.value.languages + result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    private fun loadCv() {
        viewModelScope.launch {
            when (val result = profileRepository.getCv()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(cv = result.data)
                }
                is Result.Error -> {}
            }
        }
    }

    fun uploadCv(file: java.io.File) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            when (val result = profileRepository.uploadCv(file)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(cv = result.data, isSaving = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun deleteCv() {
        viewModelScope.launch {
            when (profileRepository.deleteCv()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(cv = null)
                }
                is Result.Error -> {}
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
