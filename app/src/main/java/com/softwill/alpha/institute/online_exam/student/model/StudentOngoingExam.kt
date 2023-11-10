package com.softwill.alpha.institute.online_exam.student.model

data class StudentOngoingExam(
    val id: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val totalMarks: Int,
    val classId: Int,
    val subjectId: Int,
    val examType: String,
    val totalQuestions: Int,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val status: String?, // You might want to replace this with an appropriate data type
    val teacherAvtarUrl: String
)