package com.softwill.alpha.institute.assignment.teacher.Model


data class CreateAssignment(
    val examType: Int,
    val classId: Int,
    val subjectId: Int,
    val startDate: String,
    val endDate: String,
    val totalMarks: Int,
) : java.io.Serializable