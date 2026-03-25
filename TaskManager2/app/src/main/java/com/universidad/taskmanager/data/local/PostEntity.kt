package com.universidad.taskmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val isFavorite: Boolean = false,
    val syncStatus: String = "SYNCED",  // "SYNCED" | "PENDING_SYNC"
    val cachedAt: Long = System.currentTimeMillis()
)