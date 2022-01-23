package com.example.conduit.model

data class ArticleXXXXX(
    val author: AuthorXXXXXX,
    val body: String,
    val createdAt: String,
    val description: String,
    val favorited: Boolean,
    val favoritesCount: Int,
    val tagList: List<Any>,
    val title: String,
    val updatedAt: String
)