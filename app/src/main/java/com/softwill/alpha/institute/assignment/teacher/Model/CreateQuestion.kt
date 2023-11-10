package com.softwill.alpha.institute.assignment.teacher.Model

data class CreateQuestion(
    val question: String,
    val options: List<CreateOption>?
) : java.io.Serializable
