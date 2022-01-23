package com.example.conduit.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.conduit.data.local.offlineModel.OfflineArticle
import com.example.conduit.model.Author
import com.example.conduit.model.UserX


@Database(entities = [UserX::class, OfflineArticle::class, Author::class], version = 1)
abstract class AppDataBase :RoomDatabase() {

    abstract fun authDao(): AuthDao

}