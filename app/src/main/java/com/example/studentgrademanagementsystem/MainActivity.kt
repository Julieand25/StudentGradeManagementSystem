package com.example.studentgrademanagementsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studentgrademanagementsystem.navigation.AppNavGraph
import com.example.studentgrademanagementsystem.ui.AuthViewModel
import com.example.studentgrademanagementsystem.ui.theme.StudentGradeManagementSystemTheme
import com.example.studentgrademanagementsystem.viewmodel.StudentViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudentGradeManagementSystemTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val studentViewModel: StudentViewModel = viewModel()

                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    studentViewModel = studentViewModel
                )
            }
        }
    }
}
