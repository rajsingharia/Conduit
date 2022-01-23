package com.example.conduit.data.local.offlineModel
import androidx.room.Entity

@Entity(primaryKeys = ["type","createdAt"])
data class OfflineArticle(
    val type:String,
    val username: String,
    val body: String,
    val createdAt: String,
    val description: String,
    val favorited: Boolean,
    val favoritesCount: Int,
    val slug: String,
    val title: String,
    val updatedAt: String,
    val tagList: String
)
