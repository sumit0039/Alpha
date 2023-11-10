package com.softwill.alpha.institute.assignment.teacher.Model

data class QuestionAnswer(
    val id: Int,
    val answer: String,
    val question: String,
    var isCorrect: Boolean? = null,
    var givenMark: Int?
)
