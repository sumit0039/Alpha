package com.softwill.alpha.institute.timetable.model

data class TimeTableModel(
    val id: Int,
    val scheduleDate: String,
    val scheduleDays: String,
    val startTime: String,
    val endTime: String,
    val subjectName: String,
    val subjectShortName: String
)
