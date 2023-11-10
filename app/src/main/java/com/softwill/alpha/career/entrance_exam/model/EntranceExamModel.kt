package com.softwill.alpha.career.entrance_exam.model

import com.softwill.alpha.career.career_guidance.model.MetaDataModel

data class EntranceExamModel(
    val avtarUrl: String?, // Replace String? with the appropriate data type for avtarUrl
    val id: Int,
    val examName: String,
    val examDesc: String,
    val metaData: List<MetaDataModel>
)
