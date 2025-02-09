package com.segunfrancis.redditclient.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.redditclient.data.local.SubRedditEntity
import com.segunfrancis.redditclient.data.remote.SubredditChildren
import com.segunfrancis.redditclient.data.repo.RedditRepository
import com.segunfrancis.redditclient.data.toRedditItem
import com.segunfrancis.redditclient.ui.search.RedditItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RedditRepository
) : ViewModel() {

    private val subreddit = savedStateHandle.get<String>("subreddit")
    private val displayName = savedStateHandle.get<String>("displayName")

    private val _uiState: MutableStateFlow<DetailsUiState> = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    private val _action: MutableSharedFlow<DetailsActions> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val action: SharedFlow<DetailsActions> = _action.asSharedFlow()

    private var subRedditEntity: SubRedditEntity? = null

    init {
        subreddit?.let {
            getSingleSubredditInfo(it)
        }
        displayName?.let {
            getSubredditInfo(it)
        }
    }

    private fun getSingleSubredditInfo(subreddit: String) {
        viewModelScope.launch {
            repository.getSingleSubredditInfo(subreddit)
                .onSuccess {
                    subRedditEntity = it
                    _uiState.update { state -> state.copy(subredditItem = it?.toRedditItem()) }
                }
                .onFailure {
                    _action.tryEmit(DetailsActions.ShowMessage(message = it.localizedMessage))
                }
        }
    }

    private fun getSubredditInfo(subreddit: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            repository.getSubredditInfo(subreddit)
                .onSuccess { success ->
                    _uiState.update { it.copy(posts = success.response.children) }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(loading = false) }
                    _action.tryEmit(DetailsActions.ShowMessage(message = it.localizedMessage))
                }
            _uiState.update { it.copy(loading = false) }
        }
    }

    fun deleteSubreddit() {
        viewModelScope.launch {
            subRedditEntity?.let { entity ->
                repository.deleteSubreddit(entity)
                    .onSuccess {
                        _action.tryEmit(DetailsActions.NavigateUp)
                    }
                    .onFailure {
                        _action.tryEmit(DetailsActions.ShowMessage(message = it.localizedMessage))
                    }
            }
        }
    }
}

data class DetailsUiState(
    val subredditItem: RedditItem? = null,
    val posts: List<SubredditChildren> = emptyList(),
    val loading: Boolean = false
)

sealed class DetailsActions {
    data object NavigateUp : DetailsActions()
    data class ShowMessage(val message: String?) : DetailsActions()
}
