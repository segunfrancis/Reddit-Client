package com.segunfrancis.redditclient.data

import com.segunfrancis.redditclient.data.local.SubRedditEntity
import com.segunfrancis.redditclient.ui.search.RedditItem

fun RedditItem.toSubredditEntity(): SubRedditEntity {
    return SubRedditEntity(
        subreddit = subreddit,
        displayName = displayName,
        icon = icon,
        description = description,
        subscribersCount = subscribersCount,
        bannerBgImage = bannerBgImage
    )
}

fun SubRedditEntity.toRedditItem(): RedditItem {
    return RedditItem(
        subreddit = subreddit,
        displayName = displayName,
        icon = icon,
        description = description,
        subscribersCount = subscribersCount,
        bannerBgImage = bannerBgImage
    )
}
