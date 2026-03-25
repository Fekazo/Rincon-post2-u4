package com.universidad.taskmanager.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.universidad.taskmanager.data.local.PostDao
import com.universidad.taskmanager.data.local.PostEntity
import com.universidad.taskmanager.data.remote.PostApiService
import com.universidad.taskmanager.data.remote.toEntity
import com.universidad.taskmanager.data.workers.SyncFavoritesWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class PostRepository(
    private val dao: PostDao,
    private val api: PostApiService
) {
    companion object {
        private const val TTL_MS = 5 * 60 * 1000L  // 5 minutos
    }

    // Fuente única de verdad: la UI siempre observa Room
    fun observePosts(): Flow<List<PostEntity>> = dao.observeAll()

    // Lógica de refresco: solo llama a la red si el cache expiró
    suspend fun refreshIfStale() {
        val oldest = dao.getOldestCacheTimestamp() ?: 0L
        val isStale = (System.currentTimeMillis() - oldest) > TTL_MS
        if (isStale) {
            try {
                val remote = api.getPosts()
                dao.upsertAll(remote.map { it.toEntity() })
            } catch (e: Exception) {
                // Sin conexión: Room ya tiene datos, no se hace nada
            }
        }
    }

    suspend fun toggleFavorite(post: PostEntity) {
        dao.toggleFavorite(post.id, !post.isFavorite)

        // WorkManager encola la sincronización (ver Paso 5)
    }
    fun enqueueFavoriteSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncFavoritesWorker>()
            .setConstraints(
                Constraints(requiredNetworkType = NetworkType.CONNECTED)
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL, 15,
                TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("sync_favorites", ExistingWorkPolicy.KEEP,
                request)
    }
}