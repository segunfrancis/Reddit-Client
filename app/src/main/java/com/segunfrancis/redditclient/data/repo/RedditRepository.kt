package com.segunfrancis.redditclient.data.repo

import com.segunfrancis.redditclient.data.local.SubRedditEntity
import com.segunfrancis.redditclient.data.remote.SearchResponse
import com.segunfrancis.redditclient.data.remote.SubredditBaseResponse
import kotlinx.coroutines.flow.Flow

interface RedditRepository {
    suspend fun getSubredditInfo(subreddit: String): Result<SubredditBaseResponse>

    suspend fun searchSubreddit(subreddit: String): Result<SearchResponse>

    suspend fun addSubreddit(subreddit: SubRedditEntity)

    fun getAllSubreddits(): Flow<List<SubRedditEntity>>

    suspend fun getSingleSubredditInfo(subreddit: String): Result<SubRedditEntity?>

    suspend fun deleteSubreddit(subRedditEntity: SubRedditEntity): Result<Unit>
}
