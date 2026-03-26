package com.example.shikiflow.domain.model.user.social

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.user.User

sealed interface UserSocial

data class Follower(
    val data: User
): UserSocial

data class Thread(
    val data: Thread
): UserSocial

data class ThreadComment(
    val thread: Thread,
    val comment: Comment
): UserSocial