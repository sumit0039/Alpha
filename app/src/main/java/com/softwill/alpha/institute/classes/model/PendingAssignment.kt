package com.softwill.alpha.institute.classes.model

data class PendingAssignment(
    val id: Int,
    val totalMarks: Int,
    val teacherId: Int,
    val endDate: String,
    val startDate: String,
    val teacherName: String,
    val subjectName: String,
    val subjectShortName: String
)