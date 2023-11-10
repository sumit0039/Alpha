package com.softwill.alpha.institute.assignment.student.model

data class StudentCompletedAssignment(
    val id: Int,
    val totalMarks: Int,
    val obtainMarks: Int,
    val startDate: String,
    val endDate: String,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val teacherAvatarUrl: String
)
