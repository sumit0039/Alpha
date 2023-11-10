package com.softwill.alpha.institute.assignment.teacher.Model

data class CheckAssignment(
    val id: Int,
    val totalMarks: Int,
    val assignmentId: Int,
    val startDate: String,
    val endDate: String,
    val subjectName: String,
    val subjectShortName: String,
    val studentName: String,
    val question_answers: ArrayList<QuestionAnswer>
)
