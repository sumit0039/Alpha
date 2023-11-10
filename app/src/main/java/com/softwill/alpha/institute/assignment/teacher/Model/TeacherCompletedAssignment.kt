package com.softwill.alpha.institute.assignment.teacher.Model

data class TeacherCompletedAssignment(
    val id: Int,
    val totalMarks: Int,
    val obtainMarks: Int,
    val status: Int,
    val assignmentId: Int,
    val examType: Int,
    val startDate: String,
    val endDate: String,
    val subjectName: String,
    val subjectId: Int,
    val classId: Int,
    val subjectShortName: String,
    val studentName: String,
    val rollNumber: String?, // You might want to replace this with an appropriate data type
    val studentAvtarUrl: String
)
