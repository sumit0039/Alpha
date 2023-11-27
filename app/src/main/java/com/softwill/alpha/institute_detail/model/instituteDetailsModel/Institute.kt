package com.softwill.alpha.institute_detail.model.instituteDetailsModel

data class Institute(
    val aboutUs: String,
    val assignDeanDate: String,
    val bannerPath: String,
    val brochurePath: String,
    val createdAt: String,
    val dean: Dean,
    val deanEducation: String,
    val deanId: Int,
    val desc: String,
    val environmentRating: Double,
    val id: Int,
    val instituteRating: Double,
    val placementRating: Double,
    val policy: String,
    val staffRating: Double,
    val teachingRating: Double,
    val website: String
)