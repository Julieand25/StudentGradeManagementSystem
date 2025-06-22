package com.example.studentgrademanagementsystem.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studentgrademanagementsystem.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    fun addStudent(student: Student, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("students")
            .add(student)
            .addOnSuccessListener {
                Log.d("Firestore", "Student added with ID: ${it.id}")
                onSuccess()
                fetchStudents()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding student", e)
                onError(e)
            }
    }

    fun updateStudent(birthCertNumber: String, updatedStudent: Student, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("students")
            .whereEqualTo("birthCertNumber", birthCertNumber)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError(Exception("No student found with birthCertNumber: $birthCertNumber"))
                    return@addOnSuccessListener
                }

                val documentId = result.documents[0].id

                db.collection("students")
                    .document(documentId)
                    .set(updatedStudent)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Student updated")
                        onSuccess()
                        fetchStudents()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating student", e)
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to find student to update", e)
                onError(e)
            }
    }

    fun deleteStudent(student: Student, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("students")
            .whereEqualTo("birthCertNumber", student.birthCertNumber)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError(Exception("Student not found for deletion"))
                    return@addOnSuccessListener
                }

                for (doc in result) {
                    db.collection("students").document(doc.id).delete()
                }
                Log.d("Firestore", "Student deleted")
                fetchStudents()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting student", e)
                onError(e)
            }
    }

    fun fetchStudents() {
        db.collection("students")
            .get()
            .addOnSuccessListener { result ->
                val studentList = result.map { doc ->
                    doc.toObject(Student::class.java)
                }
                _students.value = studentList
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch students", e)
            }
    }
}

