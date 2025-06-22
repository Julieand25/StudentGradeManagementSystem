package com.example.studentgrademanagementsystem.model

import java.io.Serializable

data class Student(
    val fullName: String = "",
    val birthCertNumber: String = "",
    val dateOfBirth: String = "",
    val gradeLevel: String = "",
    val gender: String = "",
    val nationality: String = "",
    val parentContact: String = "",
    val schoolYear: String = "",
    val subjects: List<String> = emptyList()  // ✅ Fix here
) : Serializable
