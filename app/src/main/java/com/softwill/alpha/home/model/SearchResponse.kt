package com.softwill.alpha.home.model

data class SearchResponse(
    val avtarUrl: String?,
    val id: Int,
    val userTypeId: Int,
    val userName: String,
    val name: String?
)
