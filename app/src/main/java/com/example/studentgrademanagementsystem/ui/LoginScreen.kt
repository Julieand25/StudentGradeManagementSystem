package com.example.studentgrademanagementsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studentgrademanagementsystem.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isDarkTheme = isSystemInDarkTheme()
    val linkTextColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.screen_padding)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.school_logo),
            contentDescription = stringResource(id = R.string.school_logo_description),
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.logo_height))
                .padding(bottom = dimensionResource(id = R.dimen.logo_bottom_padding))
        )

        Text(
            text = stringResource(id = R.string.login_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

        Text(
            text = stringResource(id = R.string.login_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_medium)))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

        if (errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
        }

        Button(
            onClick = { onLoginClick(email, password) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.button_height)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(id = R.string.login_button), style = MaterialTheme.typography.titleMedium)
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Text(
            text = stringResource(id = R.string.forgot_password),
            color = linkTextColor,
            modifier = Modifier
                .padding(
                    top = dimensionResource(id = R.dimen.spacer_small),
                    bottom = dimensionResource(id = R.dimen.spacer_medium)
                )
                .clickable { onForgotPassword() }
        )
    }
}

@Composable
fun LoginScreenWrapper(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    navController: NavController
) {
    val state by viewModel.loginState.collectAsState()

    LoginScreen(
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        },
        onForgotPassword = {
            navController.navigate("forgot_password")
        },
        isLoading = state.isLoading,
        errorMessage = state.error
    )

    if (state.isSuccess) {
        onLoginSuccess()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = { _, _ -> },
        onForgotPassword = {},
        isLoading = false,
        errorMessage = null
    )
}
