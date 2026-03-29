package com.example.findyourjob_mobile.domain.usecase

import com.example.findyourjob_mobile.data.remote.dto.JobResponse
import com.example.findyourjob_mobile.data.repository.JobRepository
import com.example.findyourjob_mobile.data.repository.Result
import javax.inject.Inject

class GetJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(
        location: String? = null,
        jobType: String? = null,
        maxDuration: Int? = null
    ): Result<List<JobResponse>> {
        return jobRepository.getJobs(location, jobType, maxDuration)
    }
}

class GetJobDetailUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(id: Long): Result<JobResponse> {
        return jobRepository.getJob(id)
    }
}

class ApplyToJobUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(jobId: Long, coverLetter: String? = null): Result<Unit> {
        return when (val result = jobRepository.applyToJob(jobId, coverLetter)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(result.message, result.code)
        }
    }
}
