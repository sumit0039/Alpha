package com.softwill.alpha.career.career_guidance.model

import com.softwill.alpha.profile.tabActivity.PhotoModel

data class CareerGuidanceDetailModel(
    val id: Int,
    val facultyName: String,
    val streamName: String,
    val metaData: List<MetaDataModel>,
    val photos: List<PhotoModel>,
    val videos: List<VideoModel>
)