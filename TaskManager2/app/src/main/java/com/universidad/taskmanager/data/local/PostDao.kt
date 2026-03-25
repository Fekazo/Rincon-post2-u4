package com.universidad.taskmanager.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY id ASC")
    fun observeAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getPendingSync(): List<PostEntity>

    @Query("SELECT MIN(cachedAt) FROM posts")
    suspend fun getOldestCacheTimestamp(): Long?

    @Upsert
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("UPDATE posts SET isFavorite = :fav, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun toggleFavorite(id: Int, fav: Boolean)

    @Query("UPDATE posts SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Int)
}