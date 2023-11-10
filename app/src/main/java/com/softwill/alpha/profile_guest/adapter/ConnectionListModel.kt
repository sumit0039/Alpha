package com.softwill.alpha.profile_guest.adapter

data class ConnectionListModel(
    val id: Int,
    val userId: Int,
    val name: String,
    val userName: String?,
    val isMutualFriend: Int,
    val createdAt: String,
    val avtarUrl: String?
)
