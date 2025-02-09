package com.segunfrancis.redditclient.data.repo

import com.segunfrancis.redditclient.data.local.RedditClientDao
import com.segunfrancis.redditclient.data.local.SubRedditEntity
import com.segunfrancis.redditclient.data.remote.RetrofitService
import com.segunfrancis.redditclient.data.remote.SearchResponse
import com.segunfrancis.redditclient.data.remote.SubredditBaseResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RedditRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val api: RetrofitService,
    private val dao: RedditClientDao
) : RedditRepository {
    override suspend fun getSubredditInfo(subreddit: String): Result<SubredditBaseResponse> {
        return try {
            withContext(dispatcher) {
                Result.success(api.getSubredditInfo(subreddit))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun searchSubreddit(subreddit: String): Result<SearchResponse> {
        return try {
            withContext(dispatcher) {
                Result.success(api.searchSubreddit(subreddit))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun addSubreddit(subreddit: SubRedditEntity) {
        withContext(dispatcher) {
            dao.addSubreddit(subreddit = subreddit)
        }
    }

    override fun getAllSubreddits(): Flow<List<SubRedditEntity>> {
        return dao.getAllSubreddits().flowOn(dispatcher)
    }

    override suspend fun getSingleSubredditInfo(subreddit: String): Result<SubRedditEntity?> {
        return try {
            withContext(dispatcher) {
                Result.success(dao.getSubredditInfo(subreddit))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun deleteSubreddit(subRedditEntity: SubRedditEntity): Result<Unit> {
        return try {
            withContext(dispatcher) {
                Result.success(dao.deleteSubreddit(subRedditEntity))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
