package com.softwill.alpha.institute.sport.model

import com.softwill.alpha.institute.culture.model.PhotoModel
import java.io.Serializable

data class SportExhibitions(
    val id: Int,
    val title: String,
    val desc: String,
    val userName: String,
    val mobile: String,
    val email: String,
    val avtarUrl: String,
    val studentName: String,
    val photos: ArrayList<PhotoModel>
) : Serializable
