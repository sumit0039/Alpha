package com.softwill.alpha.career.mack_exam.model

data class QuestionModel(
    val id: Int,
    val question: String,
    val options: List<OptionModel>,
    var selectedAnswerId: Int = 0,
)