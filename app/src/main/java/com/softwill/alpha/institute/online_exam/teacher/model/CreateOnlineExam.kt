package com.softwill.alpha.institute.online_exam.teacher.model


data class CreateOnlineExam(
    val examTypeId: Int,
    val classId: Int,
    val subjectId: Int,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val totalMarks: Int,
) : java.io.Serializable