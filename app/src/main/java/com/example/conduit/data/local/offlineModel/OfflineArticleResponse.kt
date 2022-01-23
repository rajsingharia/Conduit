package com.example.conduit.data.local.offlineModel


data class OfflineArticleResponse (
    val type:String?,
    val username: String,
    val body: String,
    val createdAt: String,
    val description: String,
    val favorited: Boolean,
    val favoritesCount: Int,
    val slug: String,
    val title: String,
    val updatedAt: String,
    val bio: String?,
    val following: Boolean,
    val image: String,
    val tagList:String
)