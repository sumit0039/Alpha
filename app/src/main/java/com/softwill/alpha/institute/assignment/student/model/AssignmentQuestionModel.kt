package com.softwill.alpha.institute.assignment.student.model

data class AssignmentQuestionModel(
    val questionId: Int,
    val questionType: Int,
    val question: String,
    val options: List<AssignmentOptionModel>,
    var selectedAnswerId: Int = 0,
    var selectedAnswer: String,
)