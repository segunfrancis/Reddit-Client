package com.segunfrancis.redditclient.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("data")
    val searchData: SearchData,
    @SerialName("kind")
    val kind: String
)

@Serializable
data class SearchData(
    @SerialName("children")
    val children: List<Children>,
    @SerialName("dist")
    val dist: Int,
    @SerialName("geo_filter")
    val geoFilter: String,
    @SerialName("modhash")
    val modHash: String
)

@Serializable
data class Children(
    @SerialName("data")
    val subredditData: SubredditData,
    @SerialName("kind")
    val kind: String
)

@Serializable
data class SubredditData(
    @SerialName("banner_background_color")
    val bannerBackgroundColor: String = "",
    @SerialName("banner_background_image")
    val bannerBackgroundImage: String = "",
    @SerialName("banner_img")
    val bannerImg: String = "",
    @SerialName("community_icon")
    val communityIcon: String = "",
    @SerialName("created")
    val created: Double = 0.0,
    @SerialName("created_utc")
    val createdUtc: Double = 0.0,
    @SerialName("description")
    val description: String = "",
    @SerialName("description_html")
    val descriptionHtml: String = "",
    @SerialName("display_name")
    val displayName: String = "",
    @SerialName("display_name_prefixed")
    val displayNamePrefixed: String = "",
    @SerialName("header_title")
    val headerTitle: String = "",
    @SerialName("icon_img")
    val iconImg: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("lang")
    val lang: String = "",
    @SerialName("mobile_banner_image")
    val mobileBannerImage: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("primary_color")
    val primaryColor: String = "",
    @SerialName("public_description")
    val publicDescription: String = "",
    @SerialName("show_media")
    val showMedia: Boolean = false,
    @SerialName("subreddit_type")
    val subredditType: String = "",
    @SerialName("subscribers")
    val subscribers: Int = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("url")
    val url: String = ""
)

@Serializable
data class SubredditBaseResponse(
    val type: String = "",
    @SerialName("data") val response: SubRedditResponse
)

@Serializable
data class SubRedditResponse(val children: List<SubredditChildren>)

@Serializable
data class SubredditChildren(val kind: String, @SerialName("data") val post: Post)

@Serializable
data class Post(
    val subreddit: String,
    @SerialName("selftext") val selfText: String = "",
    val title: String,
    @SerialName("author_fullname") val authorFullName: String = "",
    val ups: Int,
    val downs: Int,
    val url: String = ""
)
