package com.example.findyourjob_mobile.presentation.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.findyourjob_mobile.data.remote.dto.*
import com.example.findyourjob_mobile.presentation.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatDateForApi(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}

@Composable
private fun DateField(
    label: String,
    selectedDate: Long?,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = selectedDate?.let { formatDate(it) } ?: "Sélectionner",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var expandedSections by remember { mutableStateOf(setOf("info")) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddExperienceDialog by remember { mutableStateOf(false) }
    var showAddEducationDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showAddLanguageDialog by remember { mutableStateOf(false) }
    var editingExperience by remember { mutableStateOf<ExperienceResponse?>(null) }
    var editingEducation by remember { mutableStateOf<EducationResponse?>(null) }
    var editingSkill by remember { mutableStateOf<SkillResponse?>(null) }
    var editingLanguage by remember { mutableStateOf<LanguageResponse?>(null) }
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    val fileName = if (nameIndex >= 0) cursor.getString(nameIndex) else "cv.pdf"
                    val fileSize = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L
                    
                    val inputStream = context.contentResolver.openInputStream(it)
                    val file = File(context.cacheDir, fileName)
                    inputStream?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    viewModel.uploadCv(file)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    if (showEditDialog && state.profile != null) {
        EditProfileDialog(
            profile = state.profile!!,
            isSaving = state.isSaving,
            onDismiss = { showEditDialog = false },
            onSave = { firstName, lastName, phone, location ->
                viewModel.updateProfile(firstName, lastName, phone, location)
                showEditDialog = false
            }
        )
    }

    if (showAddExperienceDialog) {
        AddExperienceDialog(
            isSaving = state.isSaving,
            onDismiss = { showAddExperienceDialog = false },
            onAdd = { title, company, description, startDate, endDate, currentJob ->
                viewModel.addExperience(title, company, description, startDate, endDate, currentJob)
                showAddExperienceDialog = false
            }
        )
    }

    if (showAddEducationDialog) {
        AddEducationDialog(
            isSaving = state.isSaving,
            onDismiss = { showAddEducationDialog = false },
            onAdd = { degree, school, fieldOfStudy, startDate, endDate ->
                viewModel.addEducation(degree, school, fieldOfStudy, startDate, endDate)
                showAddEducationDialog = false
            }
        )
    }

    if (showAddSkillDialog) {
        AddSkillDialog(
            isSaving = state.isSaving,
            onDismiss = { showAddSkillDialog = false },
            onAdd = { name, level ->
                viewModel.addSkill(name, level)
                showAddSkillDialog = false
            }
        )
    }

    if (showAddLanguageDialog) {
        AddLanguageDialog(
            isSaving = state.isSaving,
            onDismiss = { showAddLanguageDialog = false },
            onAdd = { name, level ->
                viewModel.addLanguage(name, level)
                showAddLanguageDialog = false
            }
        )
    }

    if (editingExperience != null) {
        EditExperienceDialog(
            experience = editingExperience!!,
            isSaving = state.isSaving,
            onDismiss = { editingExperience = null },
            onSave = { id, title, company, description, startDate, endDate, currentJob ->
                viewModel.updateExperience(id, title, company, description, startDate, endDate, currentJob)
                editingExperience = null
            }
        )
    }

    if (editingEducation != null) {
        EditEducationDialog(
            education = editingEducation!!,
            isSaving = state.isSaving,
            onDismiss = { editingEducation = null },
            onSave = { id, degree, school, fieldOfStudy, startDate, endDate ->
                viewModel.updateEducation(id, degree, school, fieldOfStudy, startDate, endDate)
                editingEducation = null
            }
        )
    }

    if (editingSkill != null) {
        EditSkillDialog(
            skill = editingSkill!!,
            isSaving = state.isSaving,
            onDismiss = { editingSkill = null },
            onSave = { id, name, level ->
                viewModel.updateSkill(id, name, level)
                editingSkill = null
            }
        )
    }

    if (editingLanguage != null) {
        EditLanguageDialog(
            language = editingLanguage!!,
            isSaving = state.isSaving,
            onDismiss = { editingLanguage = null },
            onSave = { id, name, level ->
                viewModel.updateLanguage(id, name, level)
                editingLanguage = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ProfileHeader(profile = state.profile)
        }

        item {
            SectionCard(
                title = "Informations",
                icon = Icons.Default.Person,
                expanded = expandedSections.contains("info"),
                onToggle = { toggleSection("info", expandedSections) { expandedSections = it } }
            ) {
                ProfileInfoContent(profile = state.profile, onEditClick = { showEditDialog = true })
            }
        }

        item {
            SectionCard(
                title = "Expériences",
                icon = Icons.Default.Work,
                count = state.experiences.size,
                expanded = expandedSections.contains("experiences"),
                onToggle = { toggleSection("experiences", expandedSections) { expandedSections = it } },
                onAddClick = { showAddExperienceDialog = true }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.experiences.forEach { exp ->
                        ExperienceItem(
                            experience = exp,
                            onDelete = { viewModel.deleteExperience(exp.id) },
                            onEdit = { editingExperience = exp }
                        )
                    }
                    if (state.experiences.isEmpty()) {
                        Text("Aucune expérience", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            SectionCard(
                title = "Formations",
                icon = Icons.Default.School,
                count = state.educations.size,
                expanded = expandedSections.contains("educations"),
                onToggle = { toggleSection("educations", expandedSections) { expandedSections = it } },
                onAddClick = { showAddEducationDialog = true }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.educations.forEach { edu ->
                        EducationItem(
                            education = edu,
                            onDelete = { viewModel.deleteEducation(edu.id) },
                            onEdit = { editingEducation = edu }
                        )
                    }
                    if (state.educations.isEmpty()) {
                        Text("Aucune formation", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            SectionCard(
                title = "Compétences",
                icon = Icons.Default.Star,
                count = state.skills.size,
                expanded = expandedSections.contains("skills"),
                onToggle = { toggleSection("skills", expandedSections) { expandedSections = it } },
                onAddClick = { showAddSkillDialog = true }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.skills.forEach { skill ->
                        SkillItem(
                            skill = skill,
                            onDelete = { viewModel.deleteSkill(skill.id) },
                            onEdit = { editingSkill = skill }
                        )
                    }
                    if (state.skills.isEmpty()) {
                        Text("Aucune compétence", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            SectionCard(
                title = "Langues",
                icon = Icons.Default.Language,
                count = state.languages.size,
                expanded = expandedSections.contains("languages"),
                onToggle = { toggleSection("languages", expandedSections) { expandedSections = it } },
                onAddClick = { showAddLanguageDialog = true }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.languages.forEach { lang ->
                        LanguageItem(
                            language = lang,
                            onDelete = { viewModel.deleteLanguage(lang.id) },
                            onEdit = { editingLanguage = lang }
                        )
                    }
                    if (state.languages.isEmpty()) {
                        Text("Aucune langue", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            CvSection(
                cv = state.cv,
                isSaving = state.isSaving,
                onUploadClick = { pdfPickerLauncher.launch("application/pdf") },
                onDeleteClick = { viewModel.deleteCv() }
            )
        }
    }
}

private fun toggleSection(section: String, current: Set<String>, setState: (Set<String>) -> Unit) {
    setState(if (current.contains(section)) current - section else current + section)
}

@Composable
fun ProfileHeader(profile: ProfileResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = buildString {
                            profile?.firstName?.firstOrNull()?.uppercaseChar()?.let { append(it) }
                            profile?.lastName?.firstOrNull()?.uppercaseChar()?.let { append(it) }
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}".trim().ifEmpty { "Utilisateur" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = profile?.roles?.firstOrNull()?.replace("ROLE_", "") ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int = 0,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAddClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (count > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = count.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Row {
                    if (onAddClick != null) {
                        IconButton(onClick = onAddClick, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter", modifier = Modifier.size(20.dp))
                        }
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ProfileInfoContent(profile: ProfileResponse?, onEditClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        profile?.email?.let { InfoRow("Email", it) }
        profile?.phone?.let { InfoRow("Téléphone", it) }
        profile?.location?.let { InfoRow("Ville", it) }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onEditClick, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Modifier")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ExperienceItem(experience: ExperienceResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(experience.title ?: "", fontWeight = FontWeight.Medium)
            Text(experience.company ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "${experience.startDate?.take(7) ?: ""} - ${if (experience.currentJob) "Présent" else experience.endDate?.take(7) ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Supprimer", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EducationItem(education: EducationResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(education.degree ?: "", fontWeight = FontWeight.Medium)
            Text(education.school ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "${education.startDate?.take(7) ?: ""} - ${education.endDate?.take(7) ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Supprimer", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SkillItem(skill: SkillResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(skill.name ?: "", fontWeight = FontWeight.Medium)
            skill.level?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Supprimer", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun LanguageItem(language: LanguageResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(language.name ?: "", fontWeight = FontWeight.Medium)
            language.level?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Supprimer", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    profile: ProfileResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String?, String?, String?, String?) -> Unit
) {
    var firstName by remember { mutableStateOf(profile.firstName ?: "") }
    var lastName by remember { mutableStateOf(profile.lastName ?: "") }
    var phone by remember { mutableStateOf(profile.phone ?: "") }
    var location by remember { mutableStateOf(profile.location ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le profil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ville") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(firstName.ifBlank { null }, lastName.ifBlank { null }, phone.ifBlank { null }, location.ifBlank { null }) },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Enregistrer")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExperienceDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String, String?, String, String?, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var currentJob by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedDateMillis
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une expérience") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre du poste *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Entreprise *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                DateField(
                    label = "Date de début *",
                    selectedDate = startDate,
                    onClick = { showStartPicker = true }
                )
                if (!currentJob) {
                    DateField(
                        label = "Date de fin",
                        selectedDate = endDate,
                        onClick = { showEndPicker = true }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = currentJob, onCheckedChange = { currentJob = it })
                    Text("Emploi actuel")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = startDate?.let { formatDateForApi(it) } ?: ""
                    val end = if (currentJob) null else endDate?.let { formatDateForApi(it) }
                    onAdd(title, company, description.ifBlank { null }, start, end, currentJob)
                },
                enabled = !isSaving && title.isNotBlank() && company.isNotBlank() && startDate != null
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEducationDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String, String?, String, String?) -> Unit
) {
    var degree by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedDateMillis
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une formation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = degree,
                    onValueChange = { degree = it },
                    label = { Text("Diplôme *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("Établissement *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fieldOfStudy,
                    onValueChange = { fieldOfStudy = it },
                    label = { Text("Domaine d'étude") },
                    modifier = Modifier.fillMaxWidth()
                )
                DateField(
                    label = "Date de début *",
                    selectedDate = startDate,
                    onClick = { showStartPicker = true }
                )
                DateField(
                    label = "Date de fin",
                    selectedDate = endDate,
                    onClick = { showEndPicker = true }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = startDate?.let { formatDateForApi(it) } ?: ""
                    val end = endDate?.let { formatDateForApi(it) }
                    onAdd(degree, school, fieldOfStudy.ifBlank { null }, start, end)
                },
                enabled = !isSaving && degree.isNotBlank() && school.isNotBlank() && startDate != null
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun AddSkillDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }

    val levels = listOf("Débutant", "Intermédiaire", "Avancé", "Expert")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une compétence") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la compétence *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Niveau", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = if (level == lvl) "" else lvl },
                            label = { Text(lvl, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name, level.ifBlank { null }) },
                enabled = !isSaving && name.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun AddLanguageDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }

    val levels = listOf("Débutant", "Intermédiaire", "Avancé", "Courant", "Natif")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une langue") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Langue *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Niveau", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = if (level == lvl) "" else lvl },
                            label = { Text(lvl, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name, level.ifBlank { null }) },
                enabled = !isSaving && name.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun CvSection(
    cv: CvResponse?,
    isSaving: Boolean,
    onUploadClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("CV", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (cv != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cv.fileName ?: "CV.pdf", fontWeight = FontWeight.Medium)
                            cv.fileSize?.let {
                                Text("${it / 1024} Ko", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Row {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Download, contentDescription = "Télécharger")
                            }
                            IconButton(onClick = onDeleteClick) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remplacer le CV")
                    }
                }
            } else {
                Text("Aucun CV uploadé", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onUploadClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Télécharger mon CV")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExperienceDialog(
    experience: ExperienceResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, String?, String, String?, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(experience.title ?: "") }
    var company by remember { mutableStateOf(experience.company ?: "") }
    var description by remember { mutableStateOf(experience.description ?: "") }
    var startDate by remember { mutableStateOf(experience.startDate) }
    var endDate by remember { mutableStateOf(experience.endDate) }
    var currentJob by remember { mutableStateOf(experience.currentJob) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = formatDateForApi(it)
                    }
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = formatDateForApi(it)
                    }
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier l'expérience") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre du poste *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Entreprise *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                DateField(
                    label = "Date de début *",
                    selectedDate = startDate?.let { parseDate(it) },
                    onClick = { showStartPicker = true }
                )
                if (!currentJob) {
                    DateField(
                        label = "Date de fin",
                        selectedDate = endDate?.let { parseDate(it) },
                        onClick = { showEndPicker = true }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = currentJob, onCheckedChange = { currentJob = it })
                    Text("Emploi actuel")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        experience.id, title, company, description.ifBlank { null },
                        startDate ?: "", if (currentJob) null else endDate, currentJob
                    )
                },
                enabled = !isSaving && title.isNotBlank() && company.isNotBlank() && !startDate.isNullOrBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEducationDialog(
    education: EducationResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, String?, String, String?) -> Unit
) {
    var degree by remember { mutableStateOf(education.degree ?: "") }
    var school by remember { mutableStateOf(education.school ?: "") }
    var fieldOfStudy by remember { mutableStateOf(education.fieldOfStudy ?: "") }
    var startDate by remember { mutableStateOf(education.startDate) }
    var endDate by remember { mutableStateOf(education.endDate) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = formatDateForApi(it)
                    }
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = formatDateForApi(it)
                    }
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la formation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = degree,
                    onValueChange = { degree = it },
                    label = { Text("Diplôme *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("Établissement *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fieldOfStudy,
                    onValueChange = { fieldOfStudy = it },
                    label = { Text("Domaine d'étude") },
                    modifier = Modifier.fillMaxWidth()
                )
                DateField(
                    label = "Date de début *",
                    selectedDate = startDate?.let { parseDate(it) },
                    onClick = { showStartPicker = true }
                )
                DateField(
                    label = "Date de fin",
                    selectedDate = endDate?.let { parseDate(it) },
                    onClick = { showEndPicker = true }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        education.id, degree, school, fieldOfStudy.ifBlank { null },
                        startDate ?: "", endDate
                    )
                },
                enabled = !isSaving && degree.isNotBlank() && school.isNotBlank() && !startDate.isNullOrBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun EditSkillDialog(
    skill: SkillResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Long, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(skill.name ?: "") }
    var level by remember { mutableStateOf(skill.level ?: "") }

    val levels = listOf("Débutant", "Intermédiaire", "Avancé", "Expert")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la compétence") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la compétence *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Niveau", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = if (level == lvl) "" else lvl },
                            label = { Text(lvl, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(skill.id, name, level.ifBlank { null }) },
                enabled = !isSaving && name.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun EditLanguageDialog(
    language: LanguageResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Long, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(language.name ?: "") }
    var level by remember { mutableStateOf(language.level ?: "") }

    val levels = listOf("Débutant", "Intermédiaire", "Avancé", "Courant", "Natif")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la langue") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Langue *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Niveau", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = if (level == lvl) "" else lvl },
                            label = { Text(lvl, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(language.id, name, level.ifBlank { null }) },
                enabled = !isSaving && name.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

fun parseDate(dateStr: String): Long? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.parse(dateStr)?.time
    } catch (e: Exception) {
        null
    }
}
