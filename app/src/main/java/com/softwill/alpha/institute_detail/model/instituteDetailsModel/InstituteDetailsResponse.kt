package com.softwill.alpha.institute_detail.model.instituteDetailsModel

data class InstituteDetailsResponse(
    val address: String,
    val avtarUrl: String,
    val bio: String,
    val createdAt: String,
    val email: String,
    val id: Int,
    val institute: Institute,
    val instituteName: String,
    val mobile: String,
    val stateId: Int,
    val userName: String,
    val stateName: String,
    val connections: Int
)