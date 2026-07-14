package com.example.shikiflow.domain.model.user.social

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.domain.model.user.User

sealed interface UserSocial

data class Follower(
    val data: User
): UserSocial

data class Thread(
    val data: ThreadShort
): UserSocial

data class ThreadComment(
    val thread: ThreadShort,
    val comment: Comment
): UserSocial