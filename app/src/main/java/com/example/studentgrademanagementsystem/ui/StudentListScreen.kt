package com.example.studentgrademanagementsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.painterResource
import com.example.studentgrademanagementsystem.R
import com.example.studentgrademanagementsystem.model.Student
import com.example.studentgrademanagementsystem.viewmodel.StudentViewModel
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(navController: NavController, studentViewModel: StudentViewModel) {
    val students by studentViewModel.students.collectAsState()

    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        studentViewModel.fetchStudents()
    }

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
                            modifier = Modifier.size(dimensionResource(id = R.dimen.logo_height))
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_student") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_student))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.student_profile_management),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.spacer_medium), vertical = dimensionResource(R.dimen.spacer_small))
            )

            LazyColumn {
                items(students) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.spacer_small)),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(dimensionResource(R.dimen.spacer_medium))
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stringResource(R.string.name_label, student.fullName))
                                Text(stringResource(R.string.grade_label, student.gradeLevel))
                            }
                            Row {
                                IconButton(onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("studentToEdit", student)
                                    navController.navigate("edit_student")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                                }
                                IconButton(onClick = {
                                    studentToDelete = student
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog && studentToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.confirm_delete_title)) },
                    text = { Text(stringResource(R.string.confirm_delete_message, studentToDelete?.fullName ?: "")) },
                    confirmButton = {
                        TextButton(onClick = {
                            studentViewModel.deleteStudent(
                                studentToDelete!!,
                                onSuccess = { showDialog = false },
                                onError = { showDialog = false }
                            )
                        }) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}
