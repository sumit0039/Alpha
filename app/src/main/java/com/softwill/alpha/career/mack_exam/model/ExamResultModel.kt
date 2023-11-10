package com.softwill.alpha.career.mack_exam.model

data class ExamResultModel(
    val id: Int,
    val totalMarks: Int,
    val obtainMarks: Int?,
    val totalQuestions: Int?,
    val examName: String,
    val subject: String,
    val scheduleStartTime: String,
    val scheduleEndTime: String
)