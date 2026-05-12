package com.example.findyourjob_mobile.presentation.screens.recruiter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.findyourjob_mobile.presentation.viewmodel.RecruiterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFormScreen(
    viewModel: RecruiterViewModel = hiltViewModel(),
    jobId: Long? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val job = state.jobs.find { it.id == jobId }
    val isEditing = jobId != null

    var title by remember { mutableStateOf(job?.title ?: "") }
    var description by remember { mutableStateOf(job?.description ?: "") }
    var company by remember { mutableStateOf(job?.company ?: "") }
    var location by remember { mutableStateOf(job?.location ?: "") }
    var salaryMin by remember { mutableStateOf(job?.salaryMin?.toString() ?: "") }
    var salaryMax by remember { mutableStateOf(job?.salaryMax?.toString() ?: "") }
    var jobType by remember { mutableStateOf(job?.jobType ?: "") }
    var workSchedule by remember { mutableStateOf(job?.workSchedule ?: "") }
    var remotePolicy by remember { mutableStateOf(job?.remotePolicy ?: "") }
    var duration by remember { mutableStateOf(job?.duration?.toString() ?: "") }

    val jobTypes = listOf("CDI", "CDD", "Stage", "Alternance", "Intérim")
    val remotePolicies = listOf("Présentiel", "Hybride", "Télétravail")

    LaunchedEffect(state.jobs) {
        if (isEditing && job != null) {
            title = job.title
            description = job.description ?: ""
            company = job.company ?: ""
            location = job.location ?: ""
            salaryMin = job.salaryMin?.toString() ?: ""
            salaryMax = job.salaryMax?.toString() ?: ""
            jobType = job.jobType ?: ""
            workSchedule = job.workSchedule ?: ""
            remotePolicy = job.remotePolicy ?: ""
            duration = job.duration?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Modifier le job" else "Créer un job") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Entreprise *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = salaryMin,
                    onValueChange = { salaryMin = it.filter { c -> c.isDigit() } },
                    label = { Text("Salaire min") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = salaryMax,
                    onValueChange = { salaryMax = it.filter { c -> c.isDigit() } },
                    label = { Text("Salaire max") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Text("Type de contrat", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                jobTypes.forEach { type ->
                    FilterChip(
                        selected = jobType == type,
                        onClick = { jobType = if (jobType == type) "" else type },
                        label = { Text(type) }
                    )
                }
            }

            Text("Politique de travail à distance", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                remotePolicies.forEach { policy ->
                    FilterChip(
                        selected = remotePolicy == policy,
                        onClick = { remotePolicy = if (remotePolicy == policy) "" else policy },
                        label = { Text(policy) }
                    )
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it.filter { c -> c.isDigit() } },
                label = { Text("Durée (en mois)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isEditing && jobId != null) {
                        viewModel.updateJob(
                            id = jobId,
                            title = title,
                            description = description.ifBlank { null },
                            company = company.ifBlank { null },
                            location = location.ifBlank { null },
                            salaryMin = salaryMin.toIntOrNull(),
                            salaryMax = salaryMax.toIntOrNull(),
                            jobType = jobType.ifBlank { null },
                            workSchedule = workSchedule.ifBlank { null },
                            remotePolicy = remotePolicy.ifBlank { null },
                            duration = duration.toIntOrNull()
                        )
                    } else {
                        viewModel.createJob(
                            title = title,
                            description = description.ifBlank { null },
                            company = company.ifBlank { null },
                            location = location.ifBlank { null },
                            salaryMin = salaryMin.toIntOrNull(),
                            salaryMax = salaryMax.toIntOrNull(),
                            jobType = jobType.ifBlank { null },
                            workSchedule = workSchedule.ifBlank { null },
                            remotePolicy = remotePolicy.ifBlank { null },
                            duration = duration.toIntOrNull()
                        )
                    }
                    onSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && company.isNotBlank() && !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isEditing) "Enregistrer" else "Créer")
                }
            }
        }
    }
}
