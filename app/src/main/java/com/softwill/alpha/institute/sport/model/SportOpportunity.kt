package com.softwill.alpha.institute.sport.model

data class SportOpportunity(
    val id: Int,
    val title: String,
    val desc: String,
    val candidates: ArrayList<Candidate>
)
