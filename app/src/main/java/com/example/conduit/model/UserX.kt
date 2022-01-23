package com.example.conduit.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserX(
    val bio: String?,
    val email: String,
    val image: String,
    val token: String,
    @PrimaryKey val username: String
)