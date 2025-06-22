package com.example.studentgrademanagementsystem.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStudentScreen(navController: NavController, studentViewModel: StudentViewModel) {
    val linkTextColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary
    val context = LocalContext.current

    val student = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Student>("studentToEdit")

    if (student == null) {
        Text(stringResource(R.string.no_student_found))
        return
    }

    var fullName by rememberSaveable { mutableStateOf(student.fullName) }
    var birthCertNumber by rememberSaveable { mutableStateOf(student.birthCertNumber) }
    var dateOfBirth by rememberSaveable { mutableStateOf(student.dateOfBirth) }
    var gender by rememberSaveable { mutableStateOf(student.gender) }
    var nationality by rememberSaveable { mutableStateOf(student.nationality) }
    var gradeLevel by rememberSaveable { mutableStateOf(student.gradeLevel) }
    var parentContact by rememberSaveable { mutableStateOf(student.parentContact) }
    var schoolYear by rememberSaveable { mutableStateOf(student.schoolYear) }
    var subjects by rememberSaveable { mutableStateOf(student.subjects) }
    var showSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var tempSelectedSubjects by remember { mutableStateOf(mutableListOf<String>()) }

    val genderOptions = listOf("Male", "Female")
    val gradeOptions = listOf("Standard 1", "Standard 2", "Standard 3", "Standard 4", "Standard 5", "Standard 6")
    val nationalityOptions = listOf("Citizen", "Non-citizen", "Partial citizen")
    val allSubjects = listOf("Mathematics", "Science", "English", "Bahasa Melayu", "Pendidikan Moral", "Pendidikan Islam").sorted()

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            dateOfBirth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.hat),
                            contentDescription = stringResource(R.string.hat_icon_description),
                            modifier = Modifier.size(150.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = birthCertNumber,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.birth_cert_number)) },
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
                modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }
            )

            var genderExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.gender)) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().clickable { genderExpanded = !genderExpanded }
                )
                ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                    genderOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            gender = it
                            genderExpanded = false
                        })
                    }
                }
            }

            var nationalityExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = nationalityExpanded, onExpandedChange = { nationalityExpanded = !nationalityExpanded }) {
                OutlinedTextField(
                    value = nationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.nationality)) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().clickable { nationalityExpanded = !nationalityExpanded }
                )
                ExposedDropdownMenu(expanded = nationalityExpanded, onDismissRequest = { nationalityExpanded = false }) {
                    nationalityOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            nationality = it
                            nationalityExpanded = false
                        })
                    }
                }
            }

            var gradeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = gradeExpanded, onExpandedChange = { gradeExpanded = !gradeExpanded }) {
                OutlinedTextField(
                    value = gradeLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.grade_level)) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().clickable { gradeExpanded = !gradeExpanded }
                )
                ExposedDropdownMenu(expanded = gradeExpanded, onDismissRequest = { gradeExpanded = false }) {
                    gradeOptions.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            gradeLevel = it
                            gradeExpanded = false
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
                modifier = Modifier.fillMaxWidth().clickable {
                    tempSelectedSubjects = subjects.toMutableList()
                    showSubjectDialog = true
                }.border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(16.dp)
            ) {
                Text(
                    text = if (subjects.isEmpty()) stringResource(R.string.select_subjects) else subjects.joinToString(),
                    color = if (subjects.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground
                )
            }

            if (showSubjectDialog) {
                AlertDialog(
                    onDismissRequest = { showSubjectDialog = false },
                    title = { Text(stringResource(R.string.select_subjects)) },
                    text = {
                        Column {
                            allSubjects.forEach { subject ->
                                val isChecked = tempSelectedSubjects.contains(subject)
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            tempSelectedSubjects = if (it) {
                                                (tempSelectedSubjects + subject).toMutableList()
                                            } else {
                                                (tempSelectedSubjects - subject).toMutableList()
                                            }
                                        }
                                    )
                                    Text(
                                        text = subject,
                                        modifier = Modifier.clickable {
                                            tempSelectedSubjects = if (isChecked) {
                                                (tempSelectedSubjects - subject).toMutableList()
                                            } else {
                                                (tempSelectedSubjects + subject).toMutableList()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            subjects = tempSelectedSubjects.toList()
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
                    val updatedStudent = student.copy(
                        fullName = fullName,
                        birthCertNumber = birthCertNumber,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        nationality = nationality,
                        gradeLevel = gradeLevel,
                        parentContact = parentContact,
                        schoolYear = schoolYear,
                        subjects = subjects
                    )
                    studentViewModel.updateStudent(
                        birthCertNumber,
                        updatedStudent,
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.student_updated_success), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(context, context.getString(R.string.student_update_failed), Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(R.string.update_student))
            }

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.cancel), color = linkTextColor)
            }
        }
    }
}