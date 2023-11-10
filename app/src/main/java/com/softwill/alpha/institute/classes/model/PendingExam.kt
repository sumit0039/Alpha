package com.softwill.alpha.institute.classes.model

data class PendingExam(
    val id: Int,
    val totalMarks: Int,
    val teacherId: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val teacherName: String,
    val subjectName: String,
    val subjectShortName: String
)