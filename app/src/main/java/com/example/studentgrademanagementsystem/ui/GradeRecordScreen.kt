package com.example.studentgrademanagementsystem.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentgrademanagementsystem.R
import com.example.studentgrademanagementsystem.model.StudentMark
import com.example.studentgrademanagementsystem.viewmodel.GradeRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeRecordScreen(viewModel: GradeRecordViewModel = viewModel()) {
    val linkTextColor = if (isSystemInDarkTheme()) {
        Color.Green
    } else {
        MaterialTheme.colorScheme.primary
    }

    val classOptions = listOf("Standard 1", "Standard 2", "Standard 3", "Standard 4", "Standard 5", "Standard 6")
    val subjectOptions = listOf("Mathematics", "English", "Science", "Bahasa Melayu", "Pendidikan Moral", "Pendidikan Islam")

    var selectedClass by remember { mutableStateOf<String?>(null) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }

    var classDropdownExpanded by remember { mutableStateOf(false) }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }

    val studentMarks = viewModel.studentMarks

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // --- CLASS DROPDOWN ---
            ExposedDropdownMenuBox(
                expanded = classDropdownExpanded,
                onExpandedChange = { classDropdownExpanded = !classDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedClass ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.class_label)) },
                    placeholder = { Text(stringResource(R.string.select_class)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = classDropdownExpanded,
                    onDismissRequest = { classDropdownExpanded = false }
                ) {
                    classOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedClass = option
                                classDropdownExpanded = false
                                if (!selectedSubject.isNullOrEmpty()) {
                                    viewModel.loadStudentMarksWithProfiles(selectedClass!!, selectedSubject!!)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SUBJECT DROPDOWN ---
            ExposedDropdownMenuBox(
                expanded = subjectDropdownExpanded,
                onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSubject ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.subject_label)) },
                    placeholder = { Text(stringResource(R.string.select_subject)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = subjectDropdownExpanded,
                    onDismissRequest = { subjectDropdownExpanded = false }
                ) {
                    subjectOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedSubject = option
                                subjectDropdownExpanded = false
                                if (!selectedClass.isNullOrEmpty()) {
                                    viewModel.loadStudentMarksWithProfiles(selectedClass!!, selectedSubject!!)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- STUDENT LIST ---
            if (!selectedClass.isNullOrEmpty() && !selectedSubject.isNullOrEmpty()) {
                if (studentMarks.isEmpty()) {
                    Text(stringResource(R.string.no_students_found))
                } else {
                    LazyColumn {
                        items(studentMarks) { student ->
                            StudentRow(
                                student = student,
                                onSave = { newMark, isAbsent ->
                                    val markInt = if (isAbsent) null else newMark?.toIntOrNull()
                                    viewModel.saveMark(
                                        gradeLevel = selectedClass!!,
                                        subject = selectedSubject!!,
                                        birthCertNumber = student.birthCertNumber,
                                        fullName = student.fullName,
                                        mark = markInt,
                                        isAbsent = isAbsent,
                                        onSuccess = {
                                            viewModel.loadStudentMarksWithProfiles(selectedClass!!, selectedSubject!!)
                                        }
                                    )
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentRow(student: StudentMark, onSave: (String?, Boolean) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = student.fullName,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (student.isAbsent) stringResource(R.string.absent) else student.mark ?: stringResource(R.string.mark_placeholder),
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = { showDialog = true }) {
            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_mark))
        }
    }

    if (showDialog) {
        EditMarkDialog(
            student = student,
            onDismiss = { showDialog = false },
            onSave = { newMark, isAbsent ->
                onSave(newMark, isAbsent)
                showDialog = false
            }
        )
    }
}

@Composable
fun EditMarkDialog(
    student: StudentMark,
    onDismiss: () -> Unit,
    onSave: (String?, Boolean) -> Unit
) {
    var mark by remember { mutableStateOf(student.mark ?: "") }
    var isAbsent by remember { mutableStateOf(student.isAbsent) }

    // Move stringResource calls inside the composable
    val absentText = stringResource(R.string.absent)
    val editMarkTitle = stringResource(R.string.edit_mark_for, student.fullName)
    val markLabel = stringResource(R.string.mark)
    val cancelText = stringResource(R.string.cancel)
    val saveText = stringResource(R.string.save)
    val absentCheckboxText = stringResource(R.string.edit_mark_dialog_absent)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(editMarkTitle) },
        text = {
            Column {
                OutlinedTextField(
                    value = mark,
                    onValueChange = { mark = it },
                    label = { Text(markLabel) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    enabled = !isAbsent,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = isAbsent,
                        onCheckedChange = {
                            isAbsent = it
                            mark = if (it) absentText else ""
                        }
                    )
                    Text(absentCheckboxText)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val finalMark = if (isAbsent) absentText else mark.trim().takeIf { it.isNotEmpty() }
                Log.d("EditMarkDialog", "Save clicked: finalMark=$finalMark, isAbsent=$isAbsent")
                onSave(finalMark, isAbsent)
            }) {
                Text(saveText)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = getLinkTextColor())
            ) {
                Text(cancelText)
            }
        }
    )
}

@Composable
fun getLinkTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color.White
    } else {
        MaterialTheme.colorScheme.primary
    }
}
