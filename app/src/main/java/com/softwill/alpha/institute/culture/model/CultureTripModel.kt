package com.softwill.alpha.institute.culture.model

data class CultureTripModel(
    val id: Int,
    val title: String,
    val tripDate: String,
    val totalStudent: Int,
    val tripDetails: String,
    val managedBy: String,
    val trip_photos: ArrayList<PhotoModel>
)
