package com.example.conduit.model

data class ArticleXX(
    val _count: Count,
    val author: AuthorX,
    val body: String,
    val createdAt: String,
    val description: String,
    val favorited: Boolean,
    val favoritedBy: List<Any>,
    val favoritesCount: Int,
    val slug: String,
    val tagList: List<String>,
    val title: String,
    val updatedAt: String
)