package com.segunfrancis.redditclient.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.redditclient.data.repo.RedditRepository
import com.segunfrancis.redditclient.data.toRedditItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(repository: RedditRepository) : ViewModel() {

    val errorMessageFlow = MutableSharedFlow<String?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val subredditsState = repository.getAllSubreddits()
        .catch { errorMessageFlow.tryEmit(it.localizedMessage) }
        .map { subFlow -> subFlow.map { sub -> sub.toRedditItem() } }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList())
}
