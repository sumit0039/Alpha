package com.softwill.alpha.home.model

import com.softwill.alpha.profile.UserModel
import com.softwill.alpha.profile.tabActivity.PhotoModel

data class HomePostModel(
    val id: Int,
    val title: String,
    val desc: String,
    val name: String,
    val username: String,
    val isMyPost : Int,
    var isLiked : Int,
    var likes: Int,
    val instituteName: String,
    var comments: Int,
    val createdAt: String,
    val photos: List<PhotoModel>,
    val user: UserModel
)