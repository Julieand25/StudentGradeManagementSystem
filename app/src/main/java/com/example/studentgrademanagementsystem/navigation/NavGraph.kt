package com.example.studentgrademanagementsystem.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentgrademanagementsystem.ui.*
import com.example.studentgrademanagementsystem.ui.DashboardScreen
import com.example.studentgrademanagementsystem.viewmodel.StudentViewModel
import androidx.compose.runtime.getValue

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel
) {
    val loginState by authViewModel.loginState.collectAsState()

    // 👇 Auto-redirect to login if signed out
    LaunchedEffect(loginState.isSuccess) {
        if (!loginState.isSuccess) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreenWrapper(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController = navController // ✅ Pass navController here
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController, authViewModel)
        }
        composable("student_list") {
            // TODO: Create StudentListScreen
        }
        composable("grade_report") {
            // TODO: Create GradeReportScreen
        }
        composable("add_student") {
            AddStudentScreen(navController, studentViewModel)
        }
        composable("student_list") {
            StudentListScreen(
                navController = navController,
                studentViewModel = studentViewModel
            )
        }
        composable("edit_student") {
            EditStudentScreen(navController = navController, studentViewModel = studentViewModel)
        }
        composable("grade_record") {
            GradeRecordScreen()
        }
        composable("edit_teacher_profile") {
            EditTeacherProfileScreen(navController)
        }
        composable("change_password") {
            ChangePasswordScreen(
                onCancel = { navController.popBackStack() },
                onPasswordChanged = {
                    navController.popBackStack() // or navigate to a success screen if you prefer
                }
            )
        }

    }
}
