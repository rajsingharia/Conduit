package com.example.conduit.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Author(
    val bio: String?,
    val following: Boolean,
    val image: String,
    @PrimaryKey val username: String
)