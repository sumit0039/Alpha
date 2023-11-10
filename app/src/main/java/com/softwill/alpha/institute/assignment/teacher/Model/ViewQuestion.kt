package com.softwill.alpha.institute.assignment.teacher.Model

data class ViewQuestion(
    val id: Int,
    val questionType: Int,
    val question: String,
    val options: List<ViewOption>?,
    val answerId : Int
)