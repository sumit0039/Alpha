package com.softwill.alpha.institute.culture.model
import java.util.*

data class CultureTeamMember(
    val id: Int,
    val email: String,
    val mobile: String,
    val name: String,
    val position: String,
    val createdAt: Date,
    val avtarUrl: String
)
