package com.softwill.alpha.institute.library.Model

data class NewsPaper(
    val thumbnailUrl: String?, // Since thumbnailUrl can be null, use nullable type (String?)
    val id: Int,
    val name: String,
    val paperUrl: String,
    val categoryName: String
)
