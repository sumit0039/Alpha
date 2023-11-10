package com.softwill.alpha.institute.online_exam.teacher.model

data class TeacherOnlineExam(
    val id: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val totalMarks: Int,
    val teacherId: Int,
    val classId: Int,
    val subjectId: Int,
    val examType: String,
    val subjectName: String,
    val subjectShortName: String,
    val totalQuestions: Int,
    val teacherName: String,
    val selfCreated: Boolean,
    val teacherAvatarUrl: String
)