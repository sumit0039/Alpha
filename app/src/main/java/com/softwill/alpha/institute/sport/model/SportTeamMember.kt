package com.softwill.alpha.institute.sport.model

data class SportTeamMember(
    val id: Int,
    val position: String,
    val name: String,
    val email: String?,
    val mobile: String,
    val picUrl: String?
)
