package com.example.studentgrademanagementsystem.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.studentgrademanagementsystem.R
import com.example.studentgrademanagementsystem.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeacherProfileScreen(navController: NavController, viewModel: TeacherViewModel = viewModel()) {
    val linkTextColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUri = it } }
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
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = stringResource(R.string.selected_image),
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(0.93f)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") }
                )
            } else if (profile.profilePhotoUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profile.profilePhotoUrl),
                    contentDescription = stringResource(R.string.profile_image),
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(0.92f)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.pick_image), color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var fullName by remember(profile) { mutableStateOf(profile.fullName) }
            var email by remember(profile) { mutableStateOf(profile.email) }
            var phoneNumber by remember(profile) { mutableStateOf(profile.phoneNumber) }
            var icNumber by remember(profile) { mutableStateOf(profile.icNumber ?: "") }
            var dateOfBirth by remember(profile) { mutableStateOf(profile.dateOfBirth ?: "") }
            var gender by remember(profile) { mutableStateOf(profile.gender ?: "") }
            var address by remember(profile) { mutableStateOf(profile.address ?: "") }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(R.string.phone_number)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = icNumber,
                onValueChange = { icNumber = it },
                label = { Text(stringResource(R.string.ic_number)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text(stringResource(R.string.date_of_birth)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text(stringResource(R.string.gender)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (imageUri != null) {
                        viewModel.uploadProfileImage(
                            imageUri = imageUri!!,
                            context = context,
                            onSuccess = { uploadedUrl ->
                                viewModel.saveProfile(
                                    fullName = fullName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    profilePhotoUrl = uploadedUrl,
                                    icNumber = icNumber,
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    address = address,
                                    onSuccess = {
                                        Toast.makeText(context, context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, context.getString(R.string.profile_failed), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onFailure = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        viewModel.saveProfile(
                            fullName = fullName,
                            email = email,
                            phoneNumber = phoneNumber,
                            profilePhotoUrl = profile.profilePhotoUrl,
                            icNumber = icNumber,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            address = address,
                            onSuccess = {
                                Toast.makeText(context, context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.profile_failed), Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isLoading) stringResource(R.string.saving) else stringResource(R.string.save))
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.cancel), color = linkTextColor)
            }
        }
    }
}
