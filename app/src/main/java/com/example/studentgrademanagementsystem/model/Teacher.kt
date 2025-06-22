package com.example.studentgrademanagementsystem.model

data class Teacher(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String = "",
    val icNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val address: String? = null
)
