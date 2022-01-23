package com.example.conduit.model

data class AuthorXX(
    val bio: Any,
    val followedBy: List<FollowedBy>,
    val following: Boolean,
    val image: String,
    val username: String
)