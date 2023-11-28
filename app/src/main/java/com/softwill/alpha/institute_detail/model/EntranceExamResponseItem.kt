package com.softwill.alpha.institute_detail.model

data class EntranceExamResponseItem(
    val createdAt: String,
    val entranceName: String,
    val examLink: String,
    val id: Int,
    val instituteFacultyId: Int,
    val merit: String,
    var isShowing: Boolean
)