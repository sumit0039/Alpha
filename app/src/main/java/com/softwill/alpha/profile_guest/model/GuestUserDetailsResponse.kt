package com.softwill.alpha.profile_guest.model

import com.softwill.alpha.profile.tabActivity.PostModel

data class GuestUserDetailsResponse(
    val avtarUrl: String?,
    val id: Int,
    val userName: String?,
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val mobile: String?,
    val dob: String?,
    val gender: String?,
    val bloodGroup: String?,
    val hobbies: List<String>?,
    val skills: List<String>?,
    val achievements: List<String>?,
    val email: String?,
    val instituteName: String?,
    val connections: Int,
    val friends: Boolean,
    val posts: List<PostModel>?
)