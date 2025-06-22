package com.example.studentgrademanagementsystem.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentgrademanagementsystem.firebase.FirestoreService
import com.example.studentgrademanagementsystem.model.StudentMark
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.text.get
import kotlin.text.set

class GradeRecordViewModel : ViewModel() {

    private val _studentMarks = mutableStateListOf<StudentMark>()
    val studentMarks: List<StudentMark> get() = _studentMarks

    fun loadStudentMarksWithProfiles(gradeLevel: String, subject: String) {
        viewModelScope.launch {
            val result = FirestoreService.getStudentsByClassAndSubject(gradeLevel, subject)
            _studentMarks.clear()
            _studentMarks.addAll(result)
        }
    }

    fun saveMark(
        gradeLevel: String,
        subject: String,
        birthCertNumber: String,
        fullName: String,
        mark: Int?,
        isAbsent: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            FirestoreService.saveStudentMark(
                gradeLevel,
                subject,
                birthCertNumber,
                fullName,
                mark,
                isAbsent
            )

            // ✅ Update local list
            updateLocalMark(
                birthCertNumber = birthCertNumber,
                newMark = mark?.toString(),
                isAbsent = isAbsent
            )

            onSuccess()
        }
    }

    private fun updateLocalMark(birthCertNumber: String, newMark: String?, isAbsent: Boolean) {
        val index = _studentMarks.indexOfFirst { it.birthCertNumber == birthCertNumber }
        if (index != -1) {
            _studentMarks[index] = _studentMarks[index].copy(mark = newMark, isAbsent = isAbsent)
        }
    }
}