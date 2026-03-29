package com.example.findyourjob_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import com.example.findyourjob_mobile.data.repository.Result
import com.example.findyourjob_mobile.domain.usecase.ApplyToJobUseCase
import com.example.findyourjob_mobile.domain.usecase.GetJobDetailUseCase
import com.example.findyourjob_mobile.domain.usecase.GetJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobListState(
    val jobs: List<JobResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class JobDetailState(
    val job: JobResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val applicationSuccess: Boolean = false
)

@HiltViewModel
class JobViewModel @Inject constructor(
    private val getJobsUseCase: GetJobsUseCase,
    private val getJobDetailUseCase: GetJobDetailUseCase,
    private val applyToJobUseCase: ApplyToJobUseCase
) : ViewModel() {

    private val _jobListState = MutableStateFlow(JobListState())
    val jobListState: StateFlow<JobListState> = _jobListState.asStateFlow()

    private val _jobDetailState = MutableStateFlow(JobDetailState())
    val jobDetailState: StateFlow<JobDetailState> = _jobDetailState.asStateFlow()

    fun loadJobs(location: String? = null, jobType: String? = null, maxDuration: Int? = null) {
        viewModelScope.launch {
            _jobListState.value = _jobListState.value.copy(isLoading = true, error = null)
            when (val result = getJobsUseCase(location, jobType, maxDuration)) {
                is Result.Success -> {
                    _jobListState.value = _jobListState.value.copy(
                        isLoading = false,
                        jobs = result.data
                    )
                }
                is Result.Error -> {
                    _jobListState.value = _jobListState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun loadJobDetail(jobId: Long) {
        viewModelScope.launch {
            _jobDetailState.value = _jobDetailState.value.copy(isLoading = true, error = null)
            when (val result = getJobDetailUseCase(jobId)) {
                is Result.Success -> {
                    _jobDetailState.value = _jobDetailState.value.copy(
                        isLoading = false,
                        job = result.data
                    )
                }
                is Result.Error -> {
                    _jobDetailState.value = _jobDetailState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun applyToJob(jobId: Long, coverLetter: String? = null) {
        viewModelScope.launch {
            _jobDetailState.value = _jobDetailState.value.copy(isLoading = true, error = null)
            when (val result = applyToJobUseCase(jobId, coverLetter)) {
                is Result.Success -> {
                    _jobDetailState.value = _jobDetailState.value.copy(
                        isLoading = false,
                        applicationSuccess = true
                    )
                }
                is Result.Error -> {
                    _jobDetailState.value = _jobDetailState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun resetApplicationState() {
        _jobDetailState.value = _jobDetailState.value.copy(applicationSuccess = false)
    }

    fun clearJobListError() {
        _jobListState.value = _jobListState.value.copy(error = null)
    }

    fun clearJobDetailError() {
        _jobDetailState.value = _jobDetailState.value.copy(error = null)
    }
}
