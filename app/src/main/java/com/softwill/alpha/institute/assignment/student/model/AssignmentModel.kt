package com.softwill.alpha.institute.assignment.student.model

data class AssignmentModel(
    val id: Int,
    val examType: Int,
    val startDate: String,
    val endDate: String,
    val totalMarks: Int,
    val subjectName: String,
    val subjectShortName: String,
    val assignment_questions: List<AssignmentQuestionModel>
)
