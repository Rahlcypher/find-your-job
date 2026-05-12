package com.example.findyourjob_mobile.presentation.screens.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.findyourjob_mobile.presentation.viewmodel.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val state by viewModel.jobDetailState.collectAsStateWithLifecycle()
    var showApplyDialog by remember { mutableStateOf(false) }
    var coverLetter by remember { mutableStateOf("") }

    LaunchedEffect(jobId) {
        viewModel.loadJobDetail(jobId)
    }

    LaunchedEffect(state.applicationSuccess) {
        if (state.applicationSuccess) {
            showApplyDialog = false
            coverLetter = ""
        }
    }

    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Postuler") },
            text = {
                Column {
                    Text("Lettre de motivation (optionnel)")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = coverLetter,
                        onValueChange = { coverLetter = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.applyToJob(jobId, coverLetter.ifBlank { null }) }
                ) {
                    Text("Envoyer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Erreur",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadJobDetail(jobId) }) {
                            Text("Réessayer")
                        }
                    }
                }
            }
            state.job != null -> {
                val job = state.job!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = job.company ?: "Entreprise",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        job.location?.let { TagInfo(icon = Icons.Default.LocationOn, value = it) }
                        job.jobType?.let { TagInfo(icon = Icons.Default.Work, value = it) }
                        job.workSchedule?.let { TagInfo(icon = Icons.Default.Schedule, value = it) }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        job.remotePolicy?.let { TagInfo(icon = Icons.Default.Home, value = it) }
                        if (job.salaryMin != null && job.salaryMax != null) {
                            TagInfo(icon = Icons.Default.AttachMoney, value = "${job.salaryMin}€-${job.salaryMax}€")
                        }
                        job.duration?.let { TagInfo(icon = Icons.Default.Timer, value = "${it} mois") }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = job.description ?: "Aucune description",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showApplyDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = job.active
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (job.active) "Postuler" else "Offre expirée")
                    }
                }
            }
        }
    }
}

@Composable
fun TagInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
