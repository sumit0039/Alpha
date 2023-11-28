package com.softwill.alpha.institute_detail.model

data class GalleriesResponseItem(
    val createdAt: String,
    val id: Int,
    val photoPath: String
) {
    override fun toString(): String {
        return "GalleriesResponseItem(createdAt='$createdAt', id=$id, photoPath='$photoPath')"
    }
}