package com.example.studentgrademanagementsystem.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.studentgrademanagementsystem.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val isDarkTheme = isSystemInDarkTheme()
    val linkTextColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = dimensionResource(id = R.dimen.screen_padding),
                vertical = dimensionResource(id = R.dimen.spacer_medium)
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large))) // Top spacing

        Image(
            painter = painterResource(id = R.drawable.forgot_password),
            contentDescription = stringResource(id = R.string.forgot_password_image_desc),
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.image_height))
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.spacer_small))
        )

        Text(
            text = stringResource(id = R.string.reset_password_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.enter_your_email)) },
            singleLine = true,
            enabled = !isSent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

        if (error != null) {
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
        }

        if (isSent) {
            Text(stringResource(id = R.string.reset_email_sent))
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
        }

        Button(
            onClick = {
                isLoading = true
                error = null
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            isSent = true
                            Toast.makeText(context, context.getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
                        } else {
                            error = task.exception?.message ?: context.getString(R.string.default_error_message)
                        }
                    }
            },
            enabled = !isLoading && !isSent,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.button_height))
        ) {
            Text(
                stringResource(id = R.string.send_reset_email),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text(
                text = stringResource(id = R.string.back_to_login),
                color = linkTextColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
