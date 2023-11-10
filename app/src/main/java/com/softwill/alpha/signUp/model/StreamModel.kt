package com.softwill.alpha.signUp.model

data class StreamModel(
    val streamId: Int,
    val streamClasses: List<StreamClassModel>,
    val streamName: String
)