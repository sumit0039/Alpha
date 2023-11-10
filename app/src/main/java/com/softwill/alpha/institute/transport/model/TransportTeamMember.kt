package com.softwill.alpha.institute.transport.model

data class TransportTeamMember(
    val id: Int,
    val position: String,
    val userId: Int,
    val email: String,
    val mobile: String,
    val name: String,
    val createdAt: String,
    val avtarUrl: String?
)
