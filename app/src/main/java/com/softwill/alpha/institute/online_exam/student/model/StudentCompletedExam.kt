package com.softwill.alpha.institute.online_exam.student.model

data class StudentCompletedExam(
    val id: Int,
    val totalMarks: Int,
    val obtainMarks: Int,
    val classId: Int,
    val subjectId: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val examType: String,
    val totalQuestions: Int,
    val solved: Int,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val teacherAvtarUrl: String
)
