package com.softwill.alpha.institute.online_exam.teacher.model

data class TeacherCompletedExam(
    val id: Int,
    val totalMarks: Int,
    val obtainMarks: Int,
    val classId: Int,
    val subjectId: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val subjectName: String,
    val subjectShortName: String,
    val totalQuestions: Int,
    val solved: Int,
    val studentName: String,
    val studentAvatarUrl: String
)