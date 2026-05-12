package com.example.findyourjob_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findyourjob_mobile.data.remote.dto.ApplicationResponse
import com.example.findyourjob_mobile.data.remote.dto.JobRequest
import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import com.example.findyourjob_mobile.data.repository.RecruiterRepository
import com.example.findyourjob_mobile.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecruiterState(
    val jobs: List<JobResponse> = emptyList(),
    val applications: List<ApplicationResponse> = emptyList(),
    val selectedJobApplications: List<ApplicationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RecruiterViewModel @Inject constructor(
    private val recruiterRepository: RecruiterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecruiterState())
    val state: StateFlow<RecruiterState> = _state.asStateFlow()

    init {
        loadMyJobs()
    }

    fun loadMyJobs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = recruiterRepository.getMyJobs()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(jobs = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadAllApplications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = recruiterRepository.getAllMyApplications()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(applications = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadApplicationsForJob(jobId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = recruiterRepository.getApplicationsForJob(jobId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(selectedJobApplications = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun createJob(
        title: String,
        description: String?,
        company: String?,
        location: String?,
        salaryMin: Int?,
        salaryMax: Int?,
        jobType: String?,
        workSchedule: String?,
        remotePolicy: String?,
        duration: Int?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = JobRequest(
                title = title,
                description = description,
                company = company,
                location = location,
                salaryMin = salaryMin,
                salaryMax = salaryMax,
                jobType = jobType,
                workSchedule = workSchedule,
                remotePolicy = remotePolicy,
                duration = duration
            )
            when (val result = recruiterRepository.createJob(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        jobs = _state.value.jobs + result.data,
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun updateJob(
        id: Long,
        title: String,
        description: String?,
        company: String?,
        location: String?,
        salaryMin: Int?,
        salaryMax: Int?,
        jobType: String?,
        workSchedule: String?,
        remotePolicy: String?,
        duration: Int?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val request = JobRequest(
                title = title,
                description = description,
                company = company,
                location = location,
                salaryMin = salaryMin,
                salaryMax = salaryMax,
                jobType = jobType,
                workSchedule = workSchedule,
                remotePolicy = remotePolicy,
                duration = duration
            )
            when (val result = recruiterRepository.updateJob(id, request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        jobs = _state.value.jobs.map { if (it.id == id) result.data else it },
                        isSaving = false
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isSaving = false, error = result.message)
                }
            }
        }
    }

    fun deleteJob(id: Long) {
        viewModelScope.launch {
            when (recruiterRepository.deleteJob(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        jobs = _state.value.jobs.filter { it.id != id }
                    )
                }
                is Result.Error -> {}
            }
        }
    }

    fun updateApplicationStatus(applicationId: Long, status: String) {
        viewModelScope.launch {
            when (val result = recruiterRepository.updateApplicationStatus(applicationId, status)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        applications = _state.value.applications.map {
                            if (it.id == applicationId) result.data else it
                        },
                        selectedJobApplications = _state.value.selectedJobApplications.map {
                            if (it.id == applicationId) result.data else it
                        }
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
