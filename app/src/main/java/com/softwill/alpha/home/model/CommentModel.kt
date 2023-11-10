package com.softwill.alpha.home.model

import com.softwill.alpha.profile.UserModel

data class CommentModel(  val id: Int,
                          val comment: String,
                          val userId: Int,
                          val name: String,
                          val username: String,
                          val isMyComment: Int,
                          val createdAt: String,
                          val user: UserModel
)
