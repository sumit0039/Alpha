package com.softwill.alpha.career.best_college.model

import com.softwill.alpha.institute_detail.model.InstituteInfoModel

data class BestCollegeModel(
    val id: Int,
    val instituteName: String,
    val state: String,
    val instituteRating: Float?,
    var isFavourite: Int,
    val user: InstituteInfoModel,
    val faculties: List<FacultyModel3>

)