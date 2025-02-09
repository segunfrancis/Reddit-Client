package com.segunfrancis.redditclient.ui.app_navigation

import kotlinx.serialization.Serializable

sealed class NavDestinations {
    @Serializable
    data object Home : NavDestinations()

    @Serializable
    data object Search : NavDestinations()

    @Serializable
    data class Details(val subreddit: String, val displayName: String) : NavDestinations()
}
