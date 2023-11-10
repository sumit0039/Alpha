package com.softwill.alpha.profile.tabActivity

data class PostModel(
    val id: Int,
    val title: String,
    val desc: String,
    val createdAt: String,
    val comments : Int,
    val likes : Int,
    var isLiked : Int,
    val photos: List<PhotoModel>
)
