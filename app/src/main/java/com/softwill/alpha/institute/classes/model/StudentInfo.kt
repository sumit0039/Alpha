package com.softwill.alpha.institute.classes.model

import com.softwill.alpha.profile.UserModel

data class StudentInfo(
    val studentId: Int,
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val userName: String?,
    val rollNumber: String?,
    val User: UserModel
)
