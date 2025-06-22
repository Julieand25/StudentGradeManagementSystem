package com.example.studentgrademanagementsystem.firebase

import com.example.studentgrademanagementsystem.model.StudentMark
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getStudentMarks(gradeLevel: String, subject: String): List<StudentMark> {
        val result = mutableListOf<StudentMark>()

        val snapshot = db.collection("grade_records")
            .document(gradeLevel)
            .collection("subjects")
            .document(subject)
            .collection("students")
            .get()
            .await()

        for (doc in snapshot.documents) {
            val data = doc.data ?: continue
            val student = StudentMark(
                fullName = data["fullName"] as? String ?: "",
                birthCertNumber = doc.id,
                mark = (data["mark"] as? Long)?.toString(), // Convert Int? to String?
                isAbsent = data["isAbsent"] as? Boolean ?: false
            )
            result.add(student)
        }

        return result
    }

    suspend fun getStudentsByClassAndSubject(gradeLevel: String, subject: String): List<StudentMark> {
        val studentsSnapshot = FirebaseFirestore.getInstance()
            .collection("students")
            .whereEqualTo("gradeLevel", gradeLevel)
            .whereArrayContains("subjects", subject)
            .get()
            .await()

        val marksSnapshot = FirebaseFirestore.getInstance()
            .collection("grade_records")
            .document(gradeLevel)
            .collection("subjects")
            .document(subject)
            .collection("students")
            .get()
            .await()

        val marksMap = marksSnapshot.documents.associateBy({ it.id }, { doc ->
            Pair(
                doc.get("mark")?.toString(),
                doc.getBoolean("isAbsent") ?: false
            )
        })

        return studentsSnapshot.documents.map { doc ->
            val birthCert = doc.getString("birthCertNumber") ?: ""
            val fullName = doc.getString("fullName") ?: ""

            val (mark, isAbsent) = marksMap[birthCert] ?: Pair(null, false)

            StudentMark(
                fullName = fullName,
                birthCertNumber = birthCert,
                mark = mark,
                isAbsent = isAbsent
            )
        }
    }



    suspend fun saveStudentMark(
        gradeLevel: String,
        subject: String,
        birthCertNumber: String,
        fullName: String,
        mark: Int?,
        isAbsent: Boolean
    ) {
        val studentRef = db.collection("grade_records")
            .document(gradeLevel)
            .collection("subjects")
            .document(subject)
            .collection("students")
            .document(birthCertNumber)

        val data = mapOf(
            "fullName" to fullName,
            "mark" to mark,
            "isAbsent" to isAbsent
        )

        studentRef.set(data).await()
    }
}

