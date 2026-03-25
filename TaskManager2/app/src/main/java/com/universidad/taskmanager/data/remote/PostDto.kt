package com.universidad.taskmanager.data.remote

import com.universidad.taskmanager.data.local.PostEntity

data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

fun PostDto.toEntity() = PostEntity(id, title, body, userId)