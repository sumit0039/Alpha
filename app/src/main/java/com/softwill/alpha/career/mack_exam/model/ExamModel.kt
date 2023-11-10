package com.softwill.alpha.career.mack_exam.model

data class ExamModel(
    val id: Int,
    val examName: String,
    val totalMarks: Int,
    val subject: String,
    val totalQuestions: Int,
    val exam_questions: List<QuestionModel>
)
