package com.segunfrancis.redditclient.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.redditclient.data.repo.RedditRepository
import com.segunfrancis.redditclient.data.toSubredditEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: RedditRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun search() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.searchSubreddit(uiState.value.searchText)
                .onSuccess { response ->
                    if (response.searchData.dist >= 1) {
                        val items = response.searchData.children.map { children ->
                            RedditItem(
                                subreddit = children.subredditData.displayNamePrefixed,
                                displayName = children.subredditData.displayName,
                                icon = children.subredditData.communityIcon.substringBefore("?"),
                                description = children.subredditData.publicDescription,
                                subscribersCount = children.subredditData.subscribers,
                                bannerBgImage = children.subredditData.bannerBackgroundImage.substringBefore("?")
                            )
                        }
                        _uiState.update { it.copy(content = SearchContent.Content(items)) }
                    } else {
                        _uiState.update { it.copy(content = SearchContent.NoContent) }
                    }
                }
                .onFailure { throwable ->
                    throwable.localizedMessage?.let { message ->
                        _uiState.update { it.copy(content = SearchContent.Error(message)) }
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setSearchText(query: String) {
        _uiState.update { it.copy(searchText = query) }
    }

    fun updateDialogState(state: Boolean) {
        _uiState.update { it.copy(dialogState = state) }
    }

    fun updateSelectedItem(item: RedditItem) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun saveSubReddit() {
        viewModelScope.launch {
            uiState.value.selectedItem?.let {
                repository.addSubreddit(it.toSubredditEntity())
            }
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val content: SearchContent = SearchContent.Default,
    val searchText: String = "",
    val dialogState: Boolean = false,
    val selectedItem: RedditItem? = null
)

sealed interface SearchContent {
    data class Content(val redditItems: List<RedditItem>) : SearchContent
    data object NoContent : SearchContent
    data class Error(val errorMessage: String) : SearchContent
    data object Default : SearchContent
}

data class RedditItem(
    val subreddit: String,
    val displayName: String,
    val icon: String,
    val description: String,
    val subscribersCount: Int,
    val bannerBgImage: String
)
