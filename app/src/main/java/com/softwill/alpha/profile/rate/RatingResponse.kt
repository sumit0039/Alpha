package com.softwill.alpha.profile.rate

data class RatingResponse(
    val id: Int,
    val instituteId: Int,
    val userId: Int,
    val placement: Double,
    val staff: Double,
    val teaching: Double,
    val environment: Double,
    val createdAt: String
)
