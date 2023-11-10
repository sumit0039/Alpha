package com.softwill.alpha.institute.culture.model
import java.util.*

data class CultureCalendarProgram(
    val id: Int,
    val title: String,
    val cultureDate: Date,
    val startTime: String,
    val endTime: String,
    val desc: String
)
