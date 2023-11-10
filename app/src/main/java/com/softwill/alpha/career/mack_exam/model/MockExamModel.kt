package com.softwill.alpha.career.mack_exam.model

data class MockExamModel(
    val id: Int,
    val examName: String,
    val totalMarks: Int,
    val subject: String,
    val totalQuestions: Int,
    val scheduleStartTime: String, // You can convert this to Date/DateTime if needed
    val scheduleEndTime: String // You can convert this to Date/DateTime if needed
)