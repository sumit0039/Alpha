package com.softwill.alpha.signUp.model

data class FacultyModel(
    val facultyId: Int,
    val name: String,
    val streams: List<StreamModel>
)