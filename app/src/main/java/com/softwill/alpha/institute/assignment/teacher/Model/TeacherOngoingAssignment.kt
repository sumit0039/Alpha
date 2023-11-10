package com.softwill.alpha.institute.assignment.teacher.Model

data class TeacherOngoingAssignment(
    val id: Int,
    val examType: Int,
    val startDate: String,
    val endDate: String,
    val totalMarks: Int,
    val teacherId: Int,
    val subjectId: Int,
    val classId: Int,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val selfCreated: Boolean,
    val teacherAvatarUrl: String?
)
