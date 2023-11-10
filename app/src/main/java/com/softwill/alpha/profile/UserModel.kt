package com.softwill.alpha.profile

data class UserModel(
    val id: Int,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val avtarUrl: String?,
    val email: String?,
    val mobile: String,
    val dob: String?,
    val gender: String?,
    val bloodGroup: String?,
    val bio: String?,
    val hobbies: List<String>?,
    val skills: List<String>?,
    val achievements: List<String>?,
    val connections: Int,
    val name: String
)