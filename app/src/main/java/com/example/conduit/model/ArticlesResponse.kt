package com.example.conduit.model

data class ArticlesResponse(
    val articles: List<Article>,
    val articlesCount: Int
)