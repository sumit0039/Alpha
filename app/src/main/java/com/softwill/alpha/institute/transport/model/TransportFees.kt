package com.softwill.alpha.institute.transport.model

import java.util.Date

data class TransportFees(
    val id: Int,
    val fromLocation: String,
    val toLocation: String,
    val desc: String,
    val fees: Int,
    val createdAt: Date
)
