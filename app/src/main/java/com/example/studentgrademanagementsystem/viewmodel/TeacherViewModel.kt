package com.example.studentgrademanagementsystem.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentgrademanagementsystem.model.Teacher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TeacherViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _profile = MutableStateFlow(Teacher())
    val profile: StateFlow<Teacher> get() = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    val userId: String? get() = auth.currentUser?.uid

    fun loadProfile() {
        userId?.let { uid ->
            viewModelScope.launch {
                val doc = db.collection("teachers").document(uid).get().await()
                _profile.value = Teacher(
                    fullName = doc.getString("fullName") ?: "",
                    email = doc.getString("email") ?: "",
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    profilePhotoUrl = doc.getString("profilePhotoUrl") ?: "",
                    icNumber = doc.getString("icNumber"),
                    dateOfBirth = doc.getString("dateOfBirth"),
                    gender = doc.getString("gender"),
                    address = doc.getString("address")
                )
            }
        }
    }

    fun uploadProfileImage(
        imageUri: Uri,
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        _isLoading.value = true
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val filename = "profile_images/${userId}-${UUID.randomUUID()}.jpg"
            val fileRef = storage.reference.child(filename)

            fileRef.putStream(inputStream!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            _isLoading.value = false
                            onSuccess(uri.toString())
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            onFailure("Failed to get image URL")
                        }
                }
                .addOnFailureListener {
                    _isLoading.value = false
                    onFailure("Failed to upload image")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            onFailure("Image upload error: ${e.message}")
        }
    }

    fun saveProfile(
        fullName: String,
        email: String,
        phoneNumber: String,
        profilePhotoUrl: String,
        icNumber: String?,
        dateOfBirth: String?,
        gender: String?,
        address: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        userId?.let { uid ->
            val data = mapOf(
                "fullName" to fullName,
                "email" to email,
                "phoneNumber" to phoneNumber,
                "profilePhotoUrl" to profilePhotoUrl,
                "icNumber" to icNumber,
                "dateOfBirth" to dateOfBirth,
                "gender" to gender,
                "address" to address
            )

            db.collection("teachers").document(uid)
                .set(data)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error") }
        }
    }
}
