package com.softwill.alpha.institute.library.Model

data class Book(
    val thumbnailUrl: String,
    val pdfUrl: String,
    val id: Int,
    val name: String,
    val writerName: String,
    val bookUrl: String?,
    val bookCategoryId: Int,
    val createdAt: String,
    val categoryName: String,
    var isSaved: Int
)
