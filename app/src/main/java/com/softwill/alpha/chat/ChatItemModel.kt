package com.softwill.alpha.chat

data class ChatItemModel(
    val name: String,
    val time: String,
    val unSeen: Boolean,
    val unSeenCount: Int
) {
}
