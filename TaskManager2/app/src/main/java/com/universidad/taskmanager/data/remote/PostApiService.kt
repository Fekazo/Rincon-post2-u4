package com.universidad.taskmanager.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PostApiService {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @PATCH("posts/{id}")
    suspend fun updateFavorite(
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): PostDto
}