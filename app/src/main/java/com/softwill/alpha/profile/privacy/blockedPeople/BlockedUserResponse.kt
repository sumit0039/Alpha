package com.softwill.alpha.profile.privacy.blockedPeople

data class BlockedUserResponse(
    val id: Int,
    val userId: Int,
    val blockUserId: Int,
    val createdAt: String,
    val blockUser: BlockedUserInfo
)
