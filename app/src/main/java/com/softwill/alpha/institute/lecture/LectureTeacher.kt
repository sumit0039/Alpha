package com.softwill.alpha.institute.lecture.model

data class LectureTeacher(
    val id: Int,
    val lectureDate: String,
    val lectureStartTime: String,
    val lectureEndTime: String,
    val className: String,
    val streamName: String,
    val subjectName: String,
    val subjectShortName: String,
    val teacherName: String,
    val teacherAvatarUrl: String
)