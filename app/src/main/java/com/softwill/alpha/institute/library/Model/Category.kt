package com.softwill.alpha.institute.library.Model

data class SavedBookCategory(
    val id: Int,
    val name: String,
    val Books: ArrayList<Book>
)
