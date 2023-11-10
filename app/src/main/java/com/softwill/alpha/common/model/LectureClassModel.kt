package com.softwill.alpha.common.model

data class LectureClassModel(
    val classId: Int,
    val className: String,
    val streamName: String,
    val class_subjects: ArrayList<LectureSubjectModel>
)
