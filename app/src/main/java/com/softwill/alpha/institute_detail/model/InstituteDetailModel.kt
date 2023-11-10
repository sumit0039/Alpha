package com.softwill.alpha.institute_detail.model

data class InstituteDetailModel(
    val id: Int,
    val instituteName: String,
    val instituteUsername: String,
    val state: String,
    val instituteRating: Double?,
    val isFavourite: Int,
    val user: InstituteInfoModel?,
    val institute_faculties: List<InstituteFacultyModel>
)