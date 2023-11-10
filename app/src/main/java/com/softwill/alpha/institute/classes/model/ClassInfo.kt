package com.softwill.alpha.institute.classes.model

data class ClassInfo (
    val streamName: String,
    val classTeacher: String?, // Change the type to the actual type of classTeacher if known
    val students: ArrayList<StudentInfo>
)