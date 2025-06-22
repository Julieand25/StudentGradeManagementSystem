package com.example.studentgrademanagementsystem.model

data class StudentMark(
    val fullName: String = "",
    val birthCertNumber: String = "",
    var mark: String? = null,
    var isAbsent: Boolean = false
)
