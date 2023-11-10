package com.softwill.alpha.institute.assignment.student.model

data class StudentOngoingAssignment(
    val id: Int,
    val examType: Int,
    val startDate: String,
    val endDate: String,
    val totalMarks: Int,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val status: String?, // You might want to replace this with an appropriate data type
    val teacherAvtarUrl: String
)
