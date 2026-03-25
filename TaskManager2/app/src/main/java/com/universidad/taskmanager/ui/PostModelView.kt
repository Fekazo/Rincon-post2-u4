package com.universidad.taskmanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.taskmanager.data.PostRepository
import com.universidad.taskmanager.data.local.PostEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(private val repo: PostRepository) : ViewModel() {

    val posts = repo.observePosts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            repo.refreshIfStale()  // Refresca solo si el cache expiró
        }
    }

    fun toggleFavorite(post: PostEntity) {
        viewModelScope.launch {
            repo.toggleFavorite(post)
        }
    }
}