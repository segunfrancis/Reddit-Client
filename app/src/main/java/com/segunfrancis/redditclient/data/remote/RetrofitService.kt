package com.segunfrancis.redditclient.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET("r/{subreddit}.json")
    suspend fun getSubredditInfo(@Path("subreddit") subreddit: String): SubredditBaseResponse

    @GET("subreddits/search.json")
    suspend fun searchSubreddit(@Query("q") subreddit: String): SearchResponse
}

const val BASE_URL: String = "https://www.reddit.com/"
