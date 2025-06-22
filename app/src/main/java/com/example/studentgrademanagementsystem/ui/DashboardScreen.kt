package com.example.studentgrademanagementsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.studentgrademanagementsystem.R
import com.example.studentgrademanagementsystem.ui.theme.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, authViewModel: AuthViewModel) {
    val linkTextColor = if (isSystemInDarkTheme()) {
        Color.Green
    } else {
        MaterialTheme.colorScheme.primary
    }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val currentUser = auth.currentUser
    var isLoggedOut by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var profilePhotoUrl by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId != null) {
            val doc = db.collection("teachers").document(userId).get().await()
            fullName = doc.getString("fullName") ?: ""
            profilePhotoUrl = doc.getString("profilePhotoUrl") ?: ""
        }
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
                            modifier = Modifier.size(dimensionResource(R.dimen.top_app_bar_icon_size))
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
                .padding(dimensionResource(R.dimen.horizontal_padding))
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.spacer_medium)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        navController.navigate("edit_teacher_profile")
                    }
                ) {
                    if (profilePhotoUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(profilePhotoUrl),
                            contentDescription = stringResource(R.string.edit_profile),
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.avatar_size))
                                .aspectRatio(0.93f)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.avatar_spacing)))
                    Column {
                        Text(fullName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.tap_to_edit_profile),
                            style = MaterialTheme.typography.labelSmall,
                            color = linkTextColor
                        )
                    }
                }

                TextButton(
                    onClick = { authViewModel.logout() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(stringResource(R.string.logout))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout_icon_description))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dashboard_spacer)))

            DashboardButton(
                iconId = R.drawable.student_icon,
                iconDesc = stringResource(R.string.student_icon_description),
                label = stringResource(R.string.student_profile_management),
                onClick = { navController.navigate("student_list") }
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_small)))

            DashboardButton(
                iconId = R.drawable.grade_icon,
                iconDesc = stringResource(R.string.grade_icon_description),
                label = stringResource(R.string.grade_record_management),
                onClick = { navController.navigate("grade_record") }
            )

            DashboardButton(
                iconId = R.drawable.change_password,
                iconDesc = stringResource(R.string.change_password_icon_description),
                label = stringResource(R.string.change_password),
                onClick = { navController.navigate("change_password") }
            )
        }
    }
}

@Composable
fun DashboardButton(
    iconId: Int,
    iconDesc: String,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.spacer_small))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = iconDesc,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.button_icon_text_spacing)))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

