package com.example.conduit.model

data class ArticleXXXX(
    val author: AuthorXXXX,
    val authorId: Int,
    val body: String,
    val createdAt: String,
    val description: String,
    val favorited: Boolean,
    val favoritedBy: List<FavoritedBy>,
    val favoritesCount: Int,
    val id: Int,
    val slug: String,
    val tagList: List<String>,
    val title: String,
    val updatedAt: String
)