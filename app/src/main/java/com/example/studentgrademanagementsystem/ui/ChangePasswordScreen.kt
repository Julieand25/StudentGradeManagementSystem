package com.example.studentgrademanagementsystem.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.studentgrademanagementsystem.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onCancel: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var step by remember { mutableStateOf(1) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val linkTextColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

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
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (step == 1) stringResource(R.string.verify_current_password) else stringResource(R.string.set_new_password),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (step == 1) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text(stringResource(R.string.enter_old_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val email = user?.email
                        if (email.isNullOrEmpty()) {
                            Toast.makeText(context, context.getString(R.string.user_email_not_found), Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        val credential = EmailAuthProvider.getCredential(email, oldPassword)
                        user.reauthenticate(credential)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    step = 2
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.incorrect_old_password),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.next))
                }
            } else {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.confirm_new_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newPassword != confirmPassword) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.passwords_do_not_match),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (newPassword.length < 6) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.password_too_short),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isLoading = true
                        user?.updatePassword(newPassword)
                            ?.addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.password_changed_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onPasswordChanged()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.password_change_failed, task.exception?.message ?: ""),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.change_password))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel), color = linkTextColor)
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
