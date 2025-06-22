package com.example.studentgrademanagementsystem.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studentgrademanagementsystem.R
import com.example.studentgrademanagementsystem.model.Student
import com.example.studentgrademanagementsystem.viewmodel.StudentViewModel
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(
    navController: NavController,
    studentViewModel: StudentViewModel,
    studentToEdit: Student? = null
) {
    val linkTextColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

    val isEditMode = studentToEdit != null

    var fullName by remember { mutableStateOf(studentToEdit?.fullName ?: "") }
    var birthCertNumber by remember { mutableStateOf(studentToEdit?.birthCertNumber ?: "") }
    var dateOfBirth by remember { mutableStateOf(studentToEdit?.dateOfBirth ?: "") }
    var parentContact by remember { mutableStateOf(studentToEdit?.parentContact ?: "") }
    var schoolYear by remember { mutableStateOf(studentToEdit?.schoolYear ?: "") }
    var subjects by remember { mutableStateOf(studentToEdit?.subjects ?: emptyList()) }

    val allSubjects = listOf("Mathematics", "Science", "English", "Bahasa Melayu", "Pendidikan Moral", "Pendidikan Islam")
    var showSubjectDialog by remember { mutableStateOf(false) }

    val gradeOptions = listOf("Standard 1", "Standard 2", "Standard 3", "Standard 4", "Standard 5", "Standard 6")
    val genderOptions = listOf("Male", "Female")
    val nationalityOptions = listOf("Citizen", "Non-citizen", "Partial citizen")

    var gradeLevel by remember { mutableStateOf(studentToEdit?.gradeLevel ?: gradeOptions[0]) }
    var gender by remember { mutableStateOf(studentToEdit?.gender ?: genderOptions[0]) }
    var nationality by remember { mutableStateOf(studentToEdit?.nationality ?: nationalityOptions[0]) }

    var expandedGrade by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }
    var expandedNationality by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dateOfBirth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hat),
                            contentDescription = stringResource(R.string.hat_icon_description),
                            modifier = Modifier.size(150.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = birthCertNumber,
                onValueChange = { birthCertNumber = it },
                label = { Text(stringResource(R.string.birth_cert_number)) },
                enabled = !isEditMode,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.date_of_birth)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = stringResource(R.string.calendar_icon_description),
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            )

            ExposedDropdownMenuBox(expanded = expandedGrade, onExpandedChange = { expandedGrade = !expandedGrade }) {
                OutlinedTextField(
                    value = gradeLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.grade_level)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrade) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedGrade, onDismissRequest = { expandedGrade = false }) {
                    gradeOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            gradeLevel = it
                            expandedGrade = false
                        })
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = expandedGender, onExpandedChange = { expandedGender = !expandedGender }) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.gender)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                    genderOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            gender = it
                            expandedGender = false
                        })
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = expandedNationality, onExpandedChange = { expandedNationality = !expandedNationality }) {
                OutlinedTextField(
                    value = nationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.nationality)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNationality) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedNationality, onDismissRequest = { expandedNationality = false }) {
                    nationalityOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            nationality = it
                            expandedNationality = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = parentContact,
                onValueChange = { parentContact = it },
                label = { Text(stringResource(R.string.parent_contact)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = schoolYear,
                onValueChange = { schoolYear = it },
                label = { Text(stringResource(R.string.school_year)) },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSubjectDialog = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (subjects.isEmpty()) stringResource(R.string.select_subjects) else subjects.joinToString(),
                    color = if (subjects.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground
                )
            }

            if (showSubjectDialog) {
                var tempSelectedSubjects by remember { mutableStateOf(subjects.toList()) }

                AlertDialog(
                    onDismissRequest = { showSubjectDialog = false },
                    title = { Text(stringResource(R.string.select_subjects)) },
                    text = {
                        Column {
                            allSubjects.forEach { subject ->
                                val isChecked = tempSelectedSubjects.contains(subject)
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            tempSelectedSubjects = if (isChecked) tempSelectedSubjects - subject else tempSelectedSubjects + subject
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            tempSelectedSubjects = if (it) tempSelectedSubjects + subject else tempSelectedSubjects - subject
                                        }
                                    )
                                    Text(text = subject)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            subjects = tempSelectedSubjects
                            showSubjectDialog = false
                        }) {
                            Text(stringResource(R.string.confirm), color = linkTextColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSubjectDialog = false }) {
                            Text(stringResource(R.string.cancel), color = linkTextColor)
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val student = Student(
                        fullName = fullName,
                        birthCertNumber = birthCertNumber,
                        dateOfBirth = dateOfBirth,
                        gradeLevel = gradeLevel,
                        gender = gender,
                        nationality = nationality,
                        parentContact = parentContact,
                        schoolYear = schoolYear,
                        subjects = subjects
                    )

                    if (isEditMode) {
                        studentViewModel.updateStudent(
                            birthCertNumber,
                            student,
                            onSuccess = { navController.popBackStack() },
                            onError = { }
                        )
                    } else {
                        studentViewModel.addStudent(
                            student = student,
                            onSuccess = { navController.popBackStack() },
                            onError = { }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) stringResource(R.string.update_student) else stringResource(R.string.save_student))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text(stringResource(R.string.cancel), color = linkTextColor)
                }
            }
        }
    }
}
