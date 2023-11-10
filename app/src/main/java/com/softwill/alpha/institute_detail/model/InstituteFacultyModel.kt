package com.softwill.alpha.institute_detail.model

data class InstituteFacultyModel(
    val id: Int,
    val facultyName: String,
    val institute_streams: ArrayList<InstituteStreamModel>,
    var isShowing: Boolean = false,
)