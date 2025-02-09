package com.segunfrancis.redditclient.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RedditClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubreddit(subreddit: SubRedditEntity)

    @Query("SELECT * FROM SubRedditEntity")
    fun getAllSubreddits(): Flow<List<SubRedditEntity>>

    @Query("SELECT * FROM SubRedditEntity WHERE subreddit = :subreddit")
    suspend fun getSubredditInfo(subreddit: String): SubRedditEntity?

    @Delete(entity = SubRedditEntity::class)
    suspend fun deleteSubreddit(subRedditEntity: SubRedditEntity)
}
