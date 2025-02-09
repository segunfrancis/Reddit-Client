package com.segunfrancis.redditclient.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubRedditEntity(
    @PrimaryKey
    val subreddit: String,
    val displayName: String,
    val icon: String,
    val description: String,
    val subscribersCount: Int,
    val bannerBgImage: String
)
